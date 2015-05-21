package it.infn.ct.aginfrasgmobile.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;

public class DialogFactory {

	/**
	 * Create a simple dialog box with OK button and a message.
	 * 
	 * @param ctx
	 *            - Context on which dialog will be shown.
	 * @param title
	 * @param message
	 * @param cancelable
	 * @param pBtnTxt
	 * @param nBtnTxt
	 * @param pAction
	 * @param nAction
	 * @return Alert dialog created.
	 */
	public static AlertDialog getDialog(Context ctx, String title,
			String message, boolean cancelable, String pBtnTxt, String nBtnTxt,
			OnClickListener pAction, OnClickListener nAction) {
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setTitle(title).setMessage(message).setCancelable(cancelable)
				.setPositiveButton(pBtnTxt, pAction)
				.setNegativeButton(nBtnTxt, nAction);
		return builder.create();
	}

	public static AlertDialog getDialog(Context ctx, String title,
			String message, boolean cancelable, int pBtnTxt, int nBtnTxt,
			OnClickListener pAction, OnClickListener nAction) {
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setTitle(title).setMessage(message).setCancelable(cancelable)
				.setPositiveButton(pBtnTxt, pAction)
				.setNegativeButton(nBtnTxt, nAction);
		return builder.create();
	}
}
