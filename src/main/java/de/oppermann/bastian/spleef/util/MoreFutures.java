package de.oppermann.bastian.spleef.util;

import org.bukkit.Bukkit;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import de.oppermann.bastian.spleef.SpleefMain;

public class MoreFutures {
	
	private MoreFutures() { }
	
	/**
	 * Add a callback to a {@link ListenableFuture}
	 * to be run on the bukkit main thread
	 * 
	 * @param plugin The plugin registering the callback
	 * @param future The {@link ListenableFuture} to add this callback
	 * @param callback The callback to be called
	 */
	public static <T> void addBukkitSyncCallback(ListenableFuture<T> future, final FutureCallback<T> callback) {
		Futures.addCallback(future, new FutureCallback<T>() {
			@Override
			public void onFailure(final Throwable cause) {
				Bukkit.getScheduler().runTask(SpleefMain.getInstance(), new Runnable() {					
					@Override
					public void run() {
						callback.onFailure(cause);
					}
				});
			}
			@Override
			public void onSuccess(final T result) {
				Bukkit.getScheduler().runTask(SpleefMain.getInstance(), new Runnable() {					
					@Override
					public void run() {
						callback.onSuccess(result);
					}
				});
			}
		});
	}
	
}