/*
 * Project: UHC
 * Class: gg.uhc.uhc.ItemStackNBTStringFetcher
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Graham Howden <graham_howden1 at yahoo.co.uk>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package gg.uhc.uhc;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class ItemStackNBTStringFetcher {

    protected static final String CRAFTBUKKIT_FORMAT = "org.bukkit.craftbukkit.%s.%s";
    protected static final String NMS_FORMAT = "net.minecraft.server.%s.%s";

    protected static Method asNMSCopyMethod;
    protected static Method saveMethod;
    protected static Constructor nbtTagCompoundConstructor;

    private ItemStackNBTStringFetcher() {}

    protected static boolean ensureNms() {
        if (nbtTagCompoundConstructor != null) {
            return true;
        }

        try {
            final String packageName = Bukkit.getServer().getClass().getPackage().getName();
            final String version = packageName.substring(packageName.lastIndexOf(".") + 1);

            final Class<?> craftItemClass = Class.forName(
                    String.format(CRAFTBUKKIT_FORMAT, version, "inventory.CraftItemStack")
            );
            final Class<?> nbtTagCompoundClass = Class.forName(
                    String.format(NMS_FORMAT, version, "NBTTagCompound")
            );
            final Class<?> nmsStackClass = Class.forName(
                    String.format(NMS_FORMAT, version, "ItemStack")
            );

            asNMSCopyMethod = craftItemClass.getDeclaredMethod("asNMSCopy", ItemStack.class);
            saveMethod = nmsStackClass.getDeclaredMethod("save", nbtTagCompoundClass);
            nbtTagCompoundConstructor = nbtTagCompoundClass.getConstructor();

            return true;
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static String readFromItemStack(ItemStack stack) {
        Preconditions.checkNotNull(stack);
        Preconditions.checkArgument(stack.getTypeId() != 0, "Stack cannot be air");

        if (!ensureNms()) {
            return null;
        }

        try {
            // copy to NMS version
            final Object nmsStack = asNMSCopyMethod.invoke(null, stack);

            // create a new NBTTagCompound to save into
            final Object newTag = nbtTagCompoundConstructor.newInstance();

            // save the NMS stack into the tag
            saveMethod.invoke(nmsStack, newTag);

            // return string representation of the tag
            return newTag.toString();
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
