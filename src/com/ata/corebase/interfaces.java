package com.ata.corebase;

import android.graphics.Bitmap;

public class interfaces {

	/**
	 * An Action about the page, like when page deleted, used in
	 * sf.Page_DoActions()
	 * 
	 * @author Pedram
	 * 
	 */
	public static interface OnPageActions {
		public void OnDelete();

		public void OnReport();

		public void OnSave(boolean Saved);

		public void OnRename(String newName);

		public void OnError();

		public void OnShare();
	}

	public static interface OnSyncDataFullItemsListner {
		public void onProgressChange(int totalCount, int proccedCount,
				int remainingCount, long totalSize, long remainingSize,
				long ProcessSize);
	}

	public static interface OnSyncDataListner {

		public void OnItemProgressChange(int progress);

		public void OnFinish();

		public void OnStart();

		public void OnStop();

	}

	public static interface OnAdapterChackChange {

		public void onCheckChange(int newCheckedCount);

	}

	public static interface OnClassroomFileActions {
		public void onRemove();

		public void OnReport();

		public void onRename(String newName);

		public void OnError();
	}

	public static interface OnClassroomRecorder {

		public void OnInit();

		public void OnStart();

		public void OnPause();

		public void OnEnd();

		public void OnTimeChange(int TimeInSec);
	}

	public static interface OnFargmentResposne {
		public void OnResponse(Object result);

		public void OnSkip();

		public void OnDone();
	}

	public static interface OnNotifcationsChanges {

		public void OnRequestsChanged();

		public void OnAnswersChanged();

		public void OnQuestionsChanged();

		public void OnNotificationsChanged();

		public void OnMessagesChanged();

		public void OnNotificationsCountChanged(int totalNew,
				int notificatiosnCount, int answersCount, int questionsCount,
				int messagesCount, int requestCount);
	}

	public static interface OnListCheckChange {
		public void OnChange();
	}

	public static interface OnPageChangeListner {
		public void OnChange(int pageNumber, int TotalPage);
	}

	public static interface OnList {

		public void OnRichedEnd();
	}

	public static interface OnFunctionResponse {

		public void OnResult(Object item);
	}

	public static interface OnPlayerEvent {

		public void OnInit();

		public void OnStart();

		public void OnStop();

		public void OnPause();

		public void OnCompelete();

		public void OnPositionChange();

		public void OnBuffering(int percent);

		public void OnDurition(int playerDurtion);

		public void OnWaitingForPlay();

	}

	public static interface OnServiceMessage {
		public void OnMessage(String text);
	}

	public static interface NotificationSocketEvent {

		public void OnConnect();

		public void OnDiscounect();

		public void OnError(String message);

	}

	public static interface OnImageLoader {

		public void onStart();

		public void onCompleted(Bitmap bitmap);

		public void onError(String error);

		public void OnNull();

	}

	public static interface OnUploadListner {

		public void onPercentChange(long procceed, long length, int percent);

		public void onStart();

		public void onCompleted();

		public void onError(String error);
	}

	public static interface OnCameraImagesSelectionChange {
		public void OnSelectetionChange(int CurrentSelectedItems);
	}

	public static interface OnDownloadListner {

		public void onPercentChange(int percent);

		public void onStart();

		public void onCompleted();

		public void onError(String error);
	}

	public static interface OnResponseListener {
		/**
		 * When we get the result from server and the response is 1 , that mean
		 * we have processed the request successfully
		 * 
		 * @param result
		 *            the result that we got from server
		 */
		public void onSuccess(String result);

		/**
		 * When we get the result from server and the response is 0 , that mean
		 * we have processed the request successfully
		 * 
		 * @param result
		 */
		public void onUnSuccess(String message);

		/**
		 * When we got the unusual response from server( something that is not
		 * encoded to JSON)
		 * 
		 * @param result
		 */
		public void onError();
		
		/**
		 * this function will call any time 
		 */
		public void Anytime(); 
	}

	public static interface onMenuResponse {
		public void onDone();

		public void OnProblem();
	}

}
