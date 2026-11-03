package org.qualet.irlredactor.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.TickingBlockEntity;

@Mixin(Level.class)
public interface WorldBlockEntityTickersAccessor
{
    @Accessor("blockEntityTickers")
    List<TickingBlockEntity> irlite$getBlockEntityTickers();
}
