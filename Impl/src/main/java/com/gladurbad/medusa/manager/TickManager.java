package com.gladurbad.medusa.manager;

import com.gladurbad.medusa.Medusa;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public final class TickManager implements Runnable {

    @Getter
    private int ticks;
    private static BukkitTask task;

    public void start() {
        assert task == null : "TickProcessor has already been started!";

        task = Bukkit.getScheduler().runTaskTimer(Medusa.INSTANCE.getPlugin(), this, 0L, 1L);
    }

    public void stop() {
        if (task == null) return;

        task.cancel();
        task = null;
    }

    @Override
    public void run() {
        ticks++;
    }
}