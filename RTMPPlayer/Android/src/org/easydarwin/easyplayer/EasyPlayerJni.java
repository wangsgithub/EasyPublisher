package org.easydarwin.easyplayer;

public class EasyPlayerJni {	
	 /**
	 * Initialized with width and height.
	 *
	 * @param ctx: get by this.getApplicationContext()
	 * @param width
	 * @param height
	 *
	 * <pre>This function must be called firstly.</pre>
	 *
	 * @return {0} if successful
	 */
	 
	 public static native int EasyPlayerInit(Object ctx, int width, int height);
	  	
	 /**
	 * Set Surface view.
	 *
	 * @param glSurface: surface view
	 *
	 * @return player handle if successful
	 */
	 public static native long EasyPlayerSetSurface(Object glSurface);
	  	  	
	 /**
	 * Set playback orientation.
	 *
	 * @param handle: return value from SetSurface()
	 * @param surOrg: current orientation, LANDSCAPE with 0, PORTRAIT 1
	 *
	 * @return {0} if successful
	 */
	 public static native int EasyPlayerSetOrientation(long handle, int surOrg);
		  	
	 /**
	 * Start playback stream
	 *
	 * @param handle: return value from SmartPlayerSetSurface()
	 * @param uri: playback uri
	 *
	 * @return {0} if successful
	 */
	 public static native int EasyPlayerStartPlayback(long handle, String uri);
		  	
	 /**
	 * Close the player instance.
	 *
	 * @param handle: return value from SmartPlayerSetSurface()
	 *
	 * @return {0} if successful
	 */
	 public static native int EasyPlayerClose(long handle);
}
