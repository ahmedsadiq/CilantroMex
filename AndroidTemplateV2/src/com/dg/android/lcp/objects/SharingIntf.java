package com.dg.android.lcp.objects;

import android.os.Handler;

/**
 * The Interface Sharing Intent of All networks.
 *
 * @author shoaibkhanzada
 * @version "1.0.2"
 */
public interface SharingIntf {

	/**
	 * Authorize.
	 *
	 * @param handler the handler
	 */
	public void authorize(Handler handler);

	/**
	 * Gets the access token.
	 *
	 * @return the access token
	 */
	public String getAccessToken();

	/**
	 * Sets the access token.
	 *
	 * @param accessToken the new access token
	 */
	public void setAccessToken(String accessToken);

	/**
	 * Sign out.
	 */
	public void signOut();

	/**
	 * Start upload.
	 *
	 * @param comments the comments
	 */
	public void startUpload(String comments);
}
