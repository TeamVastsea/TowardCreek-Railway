package com.xkball.vista_railway.network;

import com.xkball.vista_railway.network.packets.OpenCatenaryGuiPacket;
import com.xkball.vista_railway.network.packets.RequestCatenaryDataPacket;
import io.netty.buffer.ByteBuf;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public enum GCPacketFactory {
    
    INSTANCE;
    
    public static final Map<PacketType, Function<ByteBuf,GCPacket>> FACTORY = new EnumMap<>(PacketType.class);
    public static final Map<Class<? extends GCPacket>,PacketType> TYPE_TABLE = new HashMap<>();
    
    public GCPacket getPacket(byte id,ByteBuf byteBuf){
        return FACTORY.get(PacketType.VALUES[id]).apply(byteBuf);
    }
    
    
    static {
        for(PacketType type : PacketType.VALUES){
            TYPE_TABLE.put(type.aClass,type);
    
            Function<ByteBuf, GCPacket> buff = byteBuf -> {
                try {
                    return type.constructor.newInstance(byteBuf);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            };
            FACTORY.put(type,buff);
        }
    }
    public enum PacketType {
        REQUEST_CATENARY_DATA(RequestCatenaryDataPacket.class),
        OPEN_CATENARY_EDIT_GUI(OpenCatenaryGuiPacket.class);
        //KEY_EVENT_TO_SERVER(KeyEventToServerPacket.class);
        //按需加枚举类型
        //然后就可以直接按数组进行索引了
        public static final PacketType[] VALUES = PacketType.values();
      
        
        private final Class<? extends GCPacket> aClass;
        private final Constructor<? extends GCPacket> constructor;
    
        PacketType(Class<? extends GCPacket> aClass) {
            this.aClass = aClass;
            try {
                this.constructor = aClass.getConstructor(ByteBuf.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    
    
        public Class<? extends GCPacket> getaClass() {
            return aClass;
        }
    }
}
