package net.cydhra.technocracy.foundation.util

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufInputStream
import io.netty.buffer.ByteBufOutputStream
import io.netty.handler.codec.EncoderException
import net.minecraft.nbt.CompressedStreamTools
import net.minecraft.nbt.NBTSizeTracker
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentString
import java.io.IOException


@Throws(IOException::class)
fun readCompoundTag(buf: ByteBuf): NBTTagCompound? {
    val i = buf.readerIndex()
    val b0 = buf.readByte()

    return if (b0.toInt() == 0) {
        null
    } else {
        buf.readerIndex(i)

        try {
            CompressedStreamTools.read(ByteBufInputStream(buf), NBTSizeTracker(2097152L))
        } catch (ioexception: IOException) {
            throw EncoderException(ioexception)
        }

    }
}

fun writeCompoundTag(nbt: NBTTagCompound?, buf: ByteBuf) {
    if (nbt == null) {
        buf.writeByte(0)
    } else {
        try {
            CompressedStreamTools.write(nbt, ByteBufOutputStream(buf))
        } catch (ioexception: IOException) {
            throw EncoderException(ioexception)
        }
    }
}

fun ByteBuf.readString(): String {
    val length = this.readInt()
    val bytes = ByteArray(length)
    this.readBytes(bytes)
    return String(bytes)
}

fun ByteBuf.writeString(str: String) {
    writeInt(str.length)
    writeBytes(str.toByteArray())
}

fun ByteBuf.readChatComponent(): ITextComponent {
    return ITextComponent.Serializer.fromJsonLenient(readString()) ?: TextComponentString("")
}

fun ByteBuf.writeChatComponent(comp: ITextComponent) {
    writeString(ITextComponent.Serializer.componentToJson(comp))
}