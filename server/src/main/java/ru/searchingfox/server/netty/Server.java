package ru.searchingfox.server.netty;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.netty.channel.ChannelHandlerContext;
import ru.searchingfox.network.NettyServer;
import ru.searchingfox.network.packets.*;
import ru.searchingfox.network.utils.Color;
import ru.searchingfox.network.utils.Pair;
import ru.searchingfox.server.model.abstraction.*;
import ru.searchingfox.server.model.realization.LocationRealization;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server {
    AuthModel authModel;
    GameModel gameModel;

    NettyServer nettyServer;
    Thread thread;

    Cache<UUID, String> uuidPlayerCache = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build();
    Cache<UUID, String> uuidFieldCache = CacheBuilder.newBuilder().expireAfterAccess(3, TimeUnit.MINUTES).build();
    Cache<UUID, Location> uuidLocationCache = CacheBuilder.newBuilder().expireAfterAccess(3, TimeUnit.MINUTES).build();

    public Server(AuthModel authModel, GameModel gameModel) {
        this.authModel = authModel;
        this.gameModel = gameModel;
    }

    public void initServer(int port) {
        nettyServer = new NettyServer();
        thread = new Thread(() -> nettyServer.bind(port), "Server");
        thread.start();

        nettyServer.putRealization(1, this::onAuthPacket);
        nettyServer.putRealization(3, this::onRequestFields);
        nettyServer.putRealization(5, this::onSelectField);
        nettyServer.putRealization(6, this::onGameTick);
    }

    //UUID дает гарантию неповторимости
    public void onAuthPacket(Packet packet, ChannelHandlerContext ctx) {
        Packet1Login loginPacket = (Packet1Login) packet;
        AuthResult authResult = authModel.authorization(loginPacket.getUsername(), loginPacket.getPassword());

        if(authResult == AuthResult.ERROR) {
            Packet2Result answerPacket = new Packet2Result("Error", null);
            ctx.writeAndFlush(answerPacket);
            return;
        }
        UUID resultUUID = UUID.randomUUID();

        synchronized (uuidPlayerCache) {
            ConcurrentMap<UUID, String> map = uuidPlayerCache.asMap();
            map.entrySet().stream()
                    .filter((set) -> set.getValue().equals(loginPacket.getUsername()))
                    .forEach(uuidStringEntry -> uuidPlayerCache.invalidate(uuidStringEntry.getKey()));

            uuidPlayerCache.put(resultUUID, loginPacket.getUsername());
        }
        Packet2Result answerPacket = new Packet2Result("Ok", resultUUID);
        ctx.writeAndFlush(answerPacket);
    }

    public void onRequestFields(Packet packet, ChannelHandlerContext ctx) {
        try {
            List<String> fields = gameModel.getFields();
            ctx.writeAndFlush(new Packet4AnswerFields(fields));
        } catch (Exception e) {
            ctx.writeAndFlush(new Packet0Exception(new RuntimeException(e.getMessage())));
        }
    }

    public void onSelectField(Packet packet, ChannelHandlerContext ctx) {
        Packet5SelectField selectPacket = (Packet5SelectField) packet;

        try {
            if (gameModel.getFields().contains(selectPacket.getSelectedField())) {
                uuidFieldCache.put(selectPacket.getUuid(), selectPacket.getSelectedField());
            } else {
                ctx.writeAndFlush(new Packet0Exception(new RuntimeException("Field doesn't exists.")));
            }
        } catch (Exception e) {
            ctx.writeAndFlush(new Packet0Exception(new RuntimeException(e.getMessage())));
        }
    }

    public void onGameTick(Packet packet, ChannelHandlerContext ctx) {
        Packet6GameTick gameTickPacket = (Packet6GameTick) packet;

        String fieldName = uuidFieldCache.getIfPresent(gameTickPacket.getUuid());
        String playerName = uuidPlayerCache.getIfPresent(gameTickPacket.getUuid());
        if ((fieldName == null) || (playerName == null)) {
            ctx.writeAndFlush(new Packet0Exception(new RuntimeException("Your session was lost. Re-login please.")));
            return;
        }

        Field field = null;
        Player player = null;

        try {
            field = gameModel.getField(fieldName);
            player = gameModel.getPlayer(playerName);
        } catch (Exception e) {
            ctx.writeAndFlush(new Packet0Exception(new RuntimeException(e.getMessage())));
            return;
        }

        Location previousLocation = uuidLocationCache.getIfPresent(gameTickPacket.getUuid());
        if (previousLocation == null) {
            uuidLocationCache.put(gameTickPacket.getUuid(),
                    new LocationRealization(gameTickPacket.getLatitude(), gameTickPacket.getLongitude(), LocationType.DEGREES));
            ctx.writeAndFlush(new Packet7ResultTick(new ArrayList<>(), 0));
            return;
        }

        int founded = player.getFoundedLocation(field).size();
        List<Pair<Double, Color>> nearestFoxes = gameModel.getFoxes(player, field, previousLocation,
                new LocationRealization(gameTickPacket.getLatitude(), gameTickPacket.getLongitude(), LocationType.DEGREES));

        int lifes = player.getFoundedLocation(field).size() - founded;

        ctx.writeAndFlush(new Packet7ResultTick(nearestFoxes, lifes));
    }






}
