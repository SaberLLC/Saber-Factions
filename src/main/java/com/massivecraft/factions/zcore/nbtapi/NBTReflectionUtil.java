package com.massivecraft.factions.zcore.nbtapi;

import com.massivecraft.factions.zcore.nbtapi.utils.GsonWrapper;
import com.massivecraft.factions.zcore.nbtapi.utils.MethodNames;
import com.massivecraft.factions.zcore.nbtapi.utils.MinecraftVersion;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.Stack;

// TODO: finish codestyle cleanup -sgdc3
public class NBTReflectionUtil {

    private static final String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];

    private static Class<?> getCraftItemStack() {

        try {
            return Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftItemStack");
        } catch (Exception ex) {
            System.out.println("Error in ItemNBTAPI!(Outdated plugin?)");
            ex.printStackTrace();
            return null;
        }
    }

    private static Class<?> getCraftEntity() {
        try {
            return Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftEntity");
        } catch (Exception ex) {
            System.out.println("Error in ItemNBTAPI!(Outdated plugin?)");
            ex.printStackTrace();
            return null;
        }
    }

    protected static Class<?> getNBTBase() {
        try {
            return Class.forName("net.minecraft.server." + version + ".NBTBase");
        } catch (Exception ex) {
            System.out.println("Error in ItemNBTAPI!(Outdated plugin?)");
            ex.printStackTrace();
            return null;
        }
    }

    protected static Class<?> getNBTTagString() {
        try {
            return Class.forName("net.minecraft.server." + version + ".NBTTagString");
        } catch (Exception ex) {
            System.out.println("Error in ItemNBTAPI!(Outdated plugin?)");
            ex.printStackTrace();
            return null;
        }
    }

    protected static Class<?> getNMSItemStack() {
        try {
            return Class.forName("net.minecraft.server." + version + ".ItemStack");
        } catch (Exception ex) {
            System.out.println("Error in ItemNBTAPI!(Outdated plugin?)");
            ex.printStackTrace();
            return null;
        }
    }

    protected static Class<?> getNBTTagCompound() {
        try {
            return Class.forName("net.minecraft.server." + version + ".NBTTagCompound");
        } catch (Exception ex) {
            System.out.println("Error in ItemNBTAPI!(Outdated plugin?)");
            ex.printStackTrace();
            return null;
        }
    }

    protected static Class<?> getNBTCompressedStreamTools() {
        try {
            return Class.forName("net.minecraft.server." + version + ".NBTCompressedStreamTools");
        } catch (Exception ex) {
            System.out.println("Error in ItemNBTAPI!(Outdated plugin?)");
            ex.printStackTrace();
            return null;
        }
    }

    protected static Class<?> getMojangsonParser() {
        try {
            return Class.forName("net.minecraft.server." + version + ".MojangsonParser");
        } catch (Exception ex) {
            System.out.println("Error in ItemNBTAPI!(Outdated plugin?)");
            ex.printStackTrace();
            return null;
        }
    }

    protected static Class<?> getTileEntity() {
        try {
            return Class.forName("net.minecraft.server." + version + ".TileEntity");
        } catch (Exception ex) {
            System.out.println("Error in ItemNBTAPI!(Outdated plugin?)");
            ex.printStackTrace();
            return null;
        }
    }

    protected static Class<?> getCraftWorld() {
        try {
            return Class.forName("org.bukkit.craftbukkit." + version + ".CraftWorld");
        } catch (Exception ex) {
            System.out.println("Error in ItemNBTAPI!(Outdated plugin?)");
            ex.printStackTrace();
            return null;
        }
    }

    public static Object getNewNBTTag() {
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        try {
            Class<?> c = Class.forName("net.minecraft.server." + version + ".NBTTagCompound");
            return c.newInstance();
        } catch (Exception ex) {
            System.out.println("Error in ItemNBTAPI!(Outdated plugin?)");
            ex.printStackTrace();
            return null;
        }
    }

    private static Object getNewBlockPosition(int x, int y, int z) {
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        try {
            Class<?> clazz = Class.forName("net.minecraft.server." + version + ".BlockPosition");
            return clazz.getConstructor(int.class, int.class, int.class).newInstance(x, y, z);
        } catch (Exception ex) {
            System.out.println("Error in ItemNBTAPI!(Outdated plugin?)");
            ex.printStackTrace();
            return null;
        }
    }

    public static Object setNBTTag(Object NBTTag, Object NMSItem) {
        try {
            Method method = NMSItem.getClass().getMethod("setTag", NBTTag.getClass());
            method.invoke(NMSItem, NBTTag);
            return NMSItem;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static Object getNMSItemStack(ItemStack item) {
        Class<?> clazz = getCraftItemStack();

        try {
            Method method = clazz.getMethod("asNMSCopy", ItemStack.class);
            return method.invoke(clazz, item);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getNMSEntity(Entity entity) {
        Class<?> clazz = getCraftEntity();

        try {
            Method method = clazz.getMethod("getHandle");
            return method.invoke(getCraftEntity().cast(entity));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object parseNBT(String json) {
        Class<?> cis = getMojangsonParser();

        try {
            Method method = cis.getMethod("parse", String.class);
            return method.invoke(null, json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object readNBTFile(FileInputStream stream) {
        Class<?> clazz = getNBTCompressedStreamTools();

        try {
            Method method = clazz.getMethod("a", InputStream.class);
            return method.invoke(clazz, stream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object saveNBTFile(Object nbt, FileOutputStream stream) {
        Class<?> clazz = getNBTCompressedStreamTools();

        try {
            Method method = clazz.getMethod("a", getNBTTagCompound(), OutputStream.class);
            return method.invoke(clazz, nbt, stream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ItemStack getBukkitItemStack(Object item) {
        Class<?> clazz = getCraftItemStack();

        try {
            Method method = clazz.getMethod("asCraftMirror", item.getClass());
            return (ItemStack) method.invoke(clazz, item);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getItemRootNBTTagCompound(Object nmsitem) {
        Class<?> clazz = nmsitem.getClass();

        try {
            Method method = clazz.getMethod("getTag");
            return method.invoke(nmsitem);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object convertNBTCompoundtoNMSItem(NBTCompound nbtcompound) {
        Class<?> clazz = getNMSItemStack();
        try {
            return clazz.getConstructor(getNBTTagCompound()).newInstance(gettoCompount(nbtcompound.getCompound(), nbtcompound));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static NBTContainer convertNMSItemtoNBTCompound(Object nmsitem) {
        Class<?> clazz = nmsitem.getClass();

        try {
            Method method = clazz.getMethod("save", getNBTTagCompound());
            Object answer = method.invoke(nmsitem, getNewNBTTag());
            return new NBTContainer(answer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getEntityNBTTagCompound(Object nmsitem) {
        Class<?> c = nmsitem.getClass();

        try {
            Method method = c.getMethod(MethodNames.getEntityNbtGetterMethodName(), getNBTTagCompound());
            Object nbt = getNBTTagCompound().newInstance();
            Object answer = method.invoke(nmsitem, nbt);
            return answer != null ? answer : nbt;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object setEntityNBTTag(Object NBTTag, Object NMSItem) {
        try {
            Method method;
            method = NMSItem.getClass().getMethod(MethodNames.getEntityNbtSetterMethodName(), getNBTTagCompound());
            method.invoke(NMSItem, NBTTag);
            return NMSItem;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static Object getTileEntityNBTTagCompound(BlockState tile) {
        try {
            Object pos = getNewBlockPosition(tile.getX(), tile.getY(), tile.getZ());
            Object cworld = getCraftWorld().cast(tile.getWorld());
            Object nmsworld = cworld.getClass().getMethod("getHandle").invoke(cworld);
            Object o = nmsworld.getClass().getMethod("getTileEntity", pos.getClass()).invoke(nmsworld, pos);
            Method method = getTileEntity().getMethod(MethodNames.getTileDataMethodName(), getNBTTagCompound());
            Object tag = getNBTTagCompound().newInstance();
            Object answer = method.invoke(o, tag);

            return answer != null ? answer : tag;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setTileEntityNBTTagCompound(BlockState tile, Object comp) {
        try {
            Object pos = getNewBlockPosition(tile.getX(), tile.getY(), tile.getZ());
            Object cworld = getCraftWorld().cast(tile.getWorld());
            Object nmsworld = cworld.getClass().getMethod("getHandle").invoke(cworld);
            Object o = nmsworld.getClass().getMethod("getTileEntity", pos.getClass()).invoke(nmsworld, pos);
            Method method = getTileEntity().getMethod("a", getNBTTagCompound());
            method.invoke(o, comp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object getSubNBTTagCompound(Object compound, String name) {
        Class<?> c = compound.getClass();

        try {
            Method method = c.getMethod("getCompound", String.class);
            return method.invoke(compound, name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void addNBTTagCompound(NBTCompound comp, String name) {
        if (name == null) {
            remove(comp, name);
            return;
        }
        Object nbttag = comp.getCompound();
        if (nbttag == null) {
            nbttag = getNewNBTTag();
        }
        if (!valideCompound(comp)) return;
        Object workingtag = gettoCompount(nbttag, comp);

        try {
            Method method = workingtag.getClass().getMethod("set", String.class, getNBTBase());
            method.invoke(workingtag, name, getNBTTagCompound().newInstance());
            comp.setCompound(nbttag);
            return;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return;
    }

    public static Boolean valideCompound(NBTCompound comp) {
        Object root = comp.getCompound();
        if (root == null) {
            root = getNewNBTTag();
        }
        return gettoCompount(root, comp) != null;
    }

    public static Object gettoCompount(Object nbttag, NBTCompound comp) {
        Stack<String> structure = new Stack<>();
        while (comp.getParent() != null) {
            structure.add(comp.getName());
            comp = comp.getParent();
        }
        while (!structure.isEmpty()) {
            nbttag = getSubNBTTagCompound(nbttag, structure.pop());
            if (nbttag == null) {
                return null;
            }
        }
        return nbttag;
    }

    public static void addOtherNBTCompound(NBTCompound comp, NBTCompound nbtcompound) {
        Object rootnbttag = comp.getCompound();
        if (rootnbttag == null) {
            rootnbttag = getNewNBTTag();
        }
        if (!valideCompound(comp)) return;
        Object workingtag = gettoCompount(rootnbttag, comp);

        try {
            Method method = workingtag.getClass().getMethod("a", getNBTTagCompound());
            method.invoke(workingtag, nbtcompound.getCompound());
            comp.setCompound(rootnbttag);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void setString(NBTCompound comp, String key, String text) {
        if (text == null) {
            remove(comp, key);
            return;
        }
        Object rootnbttag = comp.getCompound();
        if (rootnbttag == null) {
            rootnbttag = getNewNBTTag();
        }
        if (!valideCompound(comp)) return;
        Object workingtag = gettoCompount(rootnbttag, comp);

        try {
            Method method = workingtag.getClass().getMethod("setString", String.class, String.class);
            method.invoke(workingtag, key, text);
            comp.setCompound(rootnbttag);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String getString(NBTCompound comp, String key) {
        Object rootnbttag = comp.getCompound();
        if (rootnbttag == null) {
            rootnbttag = getNewNBTTag();
        }
        if (!valideCompound(comp)) return null;
        Object workingtag = gettoCompount(rootnbttag, comp);

        try {
            Method method = workingtag.getClass().getMethod("getString", String.class);
            return (String) method.invoke(workingtag, key);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String getContent(NBTCompound comp, String key) {
        Object rootnbttag = comp.getCompound();
        if (rootnbttag == null) {
            rootnbttag = getNewNBTTag();
        }
        if (!valideCompound(comp)) return null;
        Object workingtag = gettoCompount(rootnbttag, comp);

        try {
            Method method = workingtag.getClass().getMethod("get", String.class);
            return method.invoke(workingtag, key).toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void setInt(NBTCompound comp, String key, Integer i) {
        if (i == null) {
            remove(comp, key);
            return;
        }
        Object rootnbttag = comp.getCompound();
        if (rootnbttag == null) {
            rootnbttag = getNewNBTTag();
        }
        if (!valideCompound(comp)) return;
        Object workingtag = gettoCompount(rootnbttag, comp);

        try {
            Method method = workingtag.getClass().getMethod("setInt", String.class, int.class);
            method.invoke(workingtag, key, i);
            comp.setCompound(rootnbttag);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Integer getInt(NBTCompound comp, String key) {
        Object rootnbttag = comp.getCompound();
        if (rootnbttag == null) {
            rootnbttag = getNewNBTTag();
        }
        if (!valideCompound(comp)) return null;
        Object workingtag = gettoCompount(rootnbttag, comp);

        try {
            Method method = workingtag.getClass().getMethod("getInt", String.class);
            return (Integer) method.invoke(workingtag, key);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void setByteArray(NBTCompound comp, String key, byte[] b) {
        if (b == null) {
            remove(comp, key);
            return;
        }
        Object rootnbttag = comp.getCompound();
        if (rootnbttag == null) {
            rootnbttag = getNewNBTTag();
        }
        if (!valideCompound(comp)) return;
        Object workingtag = gettoCompount(rootnbttag, comp);

        try {
            Method method = workingtag.getClass().getMethod("setByteArray", String.class, byte[].class);
            method.invoke(workingtag, key, b);
            comp.setCompound(rootnbttag);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return;
    }

    public static byte[] getByteArray(NBTCompound comp, String key) {
        Object rootnbttag = comp.getCompound();
        if (rootnbttag == null) {
            rootnbttag = getNewNBTTag();
        }
        if (!valideCompound(comp)) return null;
        Object workingtag = gettoCompount(rootnbttag, comp);

        try {
            Method method = workingtag.getClass().getMethod("getByteArray", String.class);
            return (byte[]) method.invoke(workingtag, key);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void setIntArray(NBTCompound comp, String key, int[] i) {
        if (i == null) {
            remove(comp, key);
            return;
        }
        Object rootnbttag = comp.getCompound();
        if (rootnbttag == null) {
            rootnbttag = getNewNBTTag();
        }
        if (!valideCompound(comp)) return;
        Object workingtag = gettoCompount(rootnbttag, comp);

        try {
            Method method = workingtag.getClass().getMethod("setIntArray", String.class, int[].class);
            method.invoke(workingtag, key, i);
            comp.setCompound(rootnbttag);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static int[] getIntArray(NBTCompound comp, String key) {
        Object rootnbttag = comp.getCompound();
        if (rootnbttag == null) {
            rootnbttag = getNewNBTTag();
        }
        if (!valideCompound(comp)) return null;
        Object workingtag = gettoCompount(rootnbttag, comp);

        try {
            Method method = workingtag.getClass().getMethod("getIntArray", String.class);
            return (int[]) method.invoke(workingtag, key);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void setFloat(NBTCompound comp, String key, Float f) {
        if (f == null) {
            remove(comp, key);
            return;
        }
        Object rootnbttag = comp.getCompound();
        if (rootnbttag == null) {
            rootnbttag = getNewNBTTag();
        }
        if (!valideCompound(comp)) return;
        Object workingtag = gettoCompount(rootnbttag, comp);

        try {
            Method method = workingtag.getClass().getMethod("setFloat", String.class, float.class);
            method.invoke(workingtag, key, f);
            comp.setCompound(rootnbttag);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Float getFloat(NBTCompound comp, String key) {
        Object rootnbttag = comp.getCompound();
        if (rootnbttag == null) {
            rootnbttag = getNewNBTTag();
        }
        if (!valideCompound(comp)) return null;
        Object workingtag = gettoCompount(rootnbttag, comp);

        try {
            Method method = workingtag.getClass().getMethod("getFloat", String.class);
            return (Float) method.invoke(workingtag, key);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void setLong(NBTCompound comp, String key, Long f) {
        if (f == null) {
            remove(comp, key);
            return;
        }
        Object rootnbttag = comp.getCompound();
        if (rootnbttag == null) {
            rootnbttag = getNewNBTTag();
        }
        if (!valideCompound(comp)) return;
        Object workingtag = gettoCompount(rootnbttag, comp);

        try {
            Method method = workingtag.getClass().getMethod("setLong", String.class, long.class);
            method.invoke(workingtag, key, f);
            comp.setCompound(rootnbttag);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Long getLong(NBTCompound comp, String key) {
        Object rootnbttag = comp.getCompound();
        if (rootnbttag == null) {
            rootnbttag = getNewNBTTag();
        }
        if (!valideCompound(comp)) return null;
        Object workingtag = gettoCompount(rootnbttag, comp);

        try {
            Method method = workingtag.getClass().getMethod("getLong", String.class);
            return (Long) method.invoke(workingtag, key);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void setShort(NBTCompound comp, String key, Short f) {
        if (f == null) {
            remove(comp, key);
            return;
        }
        Object rootnbttag = comp.getCompound();
        if (rootnbttag == null) {
            rootnbttag = getNewNBTTag();
        }
        if (!valideCompound(comp)) return;
        Object workingtag = gettoCompount(rootnbttag, comp);

        try {
            Method method = workingtag.getClass().getMethod("setShort", String.class, short.class);
            method.invoke(workingtag, key, f);
            comp.setCompound(rootnbttag);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Short getShort(NBTCompound comp, String key) {
        Object rootnbttag = comp.getCompound();
        if (rootnbttag == null) {
            rootnbttag = getNewNBTTag();
        }
        if (!valideCompound(comp)) return null;
        Object workingtag = gettoCompount(rootnbttag, comp);

        try {
            Method method = workingtag.getClass().getMethod("getShort", String.class);
            return (Short) method.invoke(workingtag, key);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void setByte(NBTCompound comp, String key, Byte f) {
        if (f == null) {
            remove(comp, key);
            return;
        }
        Object rootnbttag = comp.getCompound();
        if (rootnbttag == null) {
            rootnbttag = getNewNBTTag();
        }
        if (!valideCompound(comp)) return;
        Object workingtag = gettoCompount(rootnbttag, comp);

        try {
            Method method = workingtag.getClass().getMethod("setByte", String.class, byte.class);
            method.invoke(workingtag, key, f);
            comp.setCompound(rootnbttag);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Byte getByte(NBTCompound comp, String key) {
        Object rootnbttag = comp.getCompound();
        if (rootnbttag == null) {
            rootnbttag = getNewNBTTag();
        }
        if (!valideCompound(comp)) return null;
        Object workingtag = gettoCompount(rootnbttag, comp);

        try {
            Method method = workingtag.getClass().getMethod("getByte", String.class);
            return (Byte) method.invoke(workingtag, key);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void setDouble(NBTCompound comp, String key, Double d) {
        if (d == null) {
            remove(comp, key);
            return;
        }
        Object rootnbttag = comp.getCompound();
        if (rootnbttag == null) {
            rootnbttag = getNewNBTTag();
        }
        if (!valideCompound(comp)) return;
        Object workingtag = gettoCompount(rootnbttag, comp);

        try {
            Method method = workingtag.getClass().getMethod("setDouble", String.class, double.class);
            method.invoke(workingtag, key, d);
            comp.setCompound(rootnbttag);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Double getDouble(NBTCompound comp, String key) {
        Object rootnbttag = comp.getCompound();
        if (rootnbttag == null) {
            rootnbttag = getNewNBTTag();
        }
        if (!valideCompound(comp)) return null;
        Object workingtag = gettoCompount(rootnbttag, comp);

        try {
            Method method = workingtag.getClass().getMethod("getDouble", String.class);
            return (Double) method.invoke(workingtag, key);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static byte getType(NBTCompound comp, String key) {
        Object rootnbttag = comp.getCompound();
        if (rootnbttag == null) {
            rootnbttag = getNewNBTTag();
        }
        if (!valideCompound(comp)) return 0;
        Object workingtag = gettoCompount(rootnbttag, comp);

        try {
            Method method = workingtag.getClass().getMethod(MethodNames.getTypeMethodName(), String.class);
            return (byte) method.invoke(workingtag, key);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public static void setBoolean(NBTCompound comp, String key, Boolean d) {
        if (d == null) {
            remove(comp, key);
            return;
        }
        Object rootnbttag = comp.getCompound();
        if (rootnbttag == null) {
            rootnbttag = getNewNBTTag();
        }
        if (!valideCompound(comp)) return;
        Object workingtag = gettoCompount(rootnbttag, comp);

        try {
            Method method = workingtag.getClass().getMethod("setBoolean", String.class, boolean.class);
            method.invoke(workingtag, key, d);
            comp.setCompound(rootnbttag);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Boolean getBoolean(NBTCompound comp, String key) {
        Object rootnbttag = comp.getCompound();
        if (rootnbttag == null) {
            rootnbttag = getNewNBTTag();
        }
        if (!valideCompound(comp)) return null;
        Object workingtag = gettoCompount(rootnbttag, comp);

        try {
            Method method = workingtag.getClass().getMethod("getBoolean", String.class);
            return (Boolean) method.invoke(workingtag, key);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void set(NBTCompound comp, String key, Object val) {
        if (val == null) {
            remove(comp, key);
            return;
        }
        Object rootnbttag = comp.getCompound();
        if (rootnbttag == null) {
            rootnbttag = getNewNBTTag();
        }
        if (!valideCompound(comp)) {
            new Throwable("InvalideCompound").printStackTrace();
            return;
        }
        Object workingtag = gettoCompount(rootnbttag, comp);
        try {
            Method method = workingtag.getClass().getMethod("set", String.class, getNBTBase());
            method.invoke(workingtag, key, val);
            comp.setCompound(rootnbttag);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static NBTList getList(NBTCompound comp, String key, NBTType type) {
        Object rootnbttag = comp.getCompound();
        if (rootnbttag == null) {
            rootnbttag = getNewNBTTag();
        }
        if (!valideCompound(comp)) return null;
        Object workingtag = gettoCompount(rootnbttag, comp);

        try {
            Method method = workingtag.getClass().getMethod("getList", String.class, int.class);
            return new NBTList(comp, key, type, method.invoke(workingtag, key, type.getId()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void setObject(NBTCompound comp, String key, Object value) {
        if (!MinecraftVersion.hasGsonSupport()) return;
        try {
            String json = GsonWrapper.getString(value);
            setString(comp, key, json);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static <T> T getObject(NBTCompound comp, String key, Class<T> type) {
        if (!MinecraftVersion.hasGsonSupport()) return null;
        String json = getString(comp, key);

        return json != null ? GsonWrapper.deserializeJson(json, type) : null;
    }

    public static void remove(NBTCompound comp, String key) {
        Object rootnbttag = comp.getCompound();
        if (rootnbttag == null) {
            rootnbttag = getNewNBTTag();
        }
        if (!valideCompound(comp)) return;
        Object workingtag = gettoCompount(rootnbttag, comp);

        try {
            Method method = workingtag.getClass().getMethod("remove", String.class);
            method.invoke(workingtag, key);
            comp.setCompound(rootnbttag);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Boolean hasKey(NBTCompound comp, String key) {
        Object rootnbttag = comp.getCompound();
        if (rootnbttag == null) {
            rootnbttag = getNewNBTTag();
        }
        if (!valideCompound(comp)) return null;
        Object workingtag = gettoCompount(rootnbttag, comp);

        try {
            Method method = workingtag.getClass().getMethod("hasKey", String.class);
            return (Boolean) method.invoke(workingtag, key);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static Set<String> getKeys(NBTCompound comp) {
        Object rootnbttag = comp.getCompound();
        if (rootnbttag == null) {
            rootnbttag = getNewNBTTag();
        }
        if (!valideCompound(comp)) return null;
        Object workingtag = gettoCompount(rootnbttag, comp);
        Method method;
        try {
            method = workingtag.getClass().getMethod("c");
            return (Set<String>) method.invoke(workingtag);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
