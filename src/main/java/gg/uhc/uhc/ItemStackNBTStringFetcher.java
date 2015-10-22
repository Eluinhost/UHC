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

public class ItemStackNBTStringFetcher {

    protected static Method asNMSCopyMethod;
    protected static Method saveMethod;
    protected static Constructor nbtTagCompoundConstructor;

    protected static boolean ensureNMS() {
        if (nbtTagCompoundConstructor != null) {
            return true;
        }

        try {
            String packageName = Bukkit.getServer().getClass().getPackage().getName();
            String version = packageName.substring(packageName.lastIndexOf(".") + 1);

            Class<?> craftItemClass = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftItemStack");
            Class<?> nbtTagCompoundClass = Class.forName("net.minecraft.server." + version + ".NBTTagCompound");
            Class<?> nmsStackClass = Class.forName("net.minecraft.server." + version + ".ItemStack");

            asNMSCopyMethod = craftItemClass.getDeclaredMethod("asNMSCopy", ItemStack.class);
            saveMethod = nmsStackClass.getDeclaredMethod("save", nbtTagCompoundClass);
            nbtTagCompoundConstructor = nbtTagCompoundClass.getConstructor();

            return true;
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String readFromItemStack(ItemStack stack) {
        Preconditions.checkNotNull(stack);
        Preconditions.checkArgument(stack.getTypeId() != 0, "Stack cannot be air");

        if (!ensureNMS()) {
            return null;
        }

        try {
            // copy to NMS version
            Object nmsStack = asNMSCopyMethod.invoke(null, stack);

            // create a new NBTTagCompound to save into
            Object newTag = nbtTagCompoundConstructor.newInstance();

            // save the NMS stack into the tag
            saveMethod.invoke(nmsStack, newTag);

            // return string representation of the tag
            return newTag.toString();
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
            return null;
        }
    }
}
