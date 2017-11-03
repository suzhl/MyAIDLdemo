package com.su.myaidldemo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class BookManagerService extends Service {

    private static final String TAG = "DEBUG-WCL" + BookManagerService.class.getSimpleName();

    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();
    private RemoteCallbackList<IOnNewBookArrivedListener> mListenerList = new RemoteCallbackList<>();
    private AtomicBoolean mIsServiceDestroyed = new AtomicBoolean(false);

    private Binder mBinder = new IBookManager.Stub() {

        @Override
        public List<Book> getBookList() throws RemoteException {
            SystemClock.sleep(1000);
            return mBookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            mBookList.add(book);
        }

        @Override
        public void registerListener(IOnNewBookArrivedListener listener) throws RemoteException {
            mListenerList.register(listener);
            int num = mListenerList.beginBroadcast();
            mListenerList.finishBroadcast();
            Log.e(TAG, "添加完成，注册接口数：" + num);
        }

        @Override
        public void unregisterListener(IOnNewBookArrivedListener listener) throws RemoteException {
            mListenerList.unregister(listener);
            int i = mListenerList.beginBroadcast();
            mListenerList.finishBroadcast();
            Log.e(TAG, "删除完成， 注册接口数：" + i);
        }
    };

    private void onNewBookArrived(Book book) throws RemoteException {
        mBookList.add(book);
        Log.e(TAG, "发送通知的数量: " + mBookList.size());
        int num = mListenerList.beginBroadcast();
        for (int i = 0; i < num; ++i) {
            IOnNewBookArrivedListener listener = mListenerList.getBroadcastItem(i);
            Log.e(TAG, "发送通知: " + listener.toString());
            listener.onNewBookArrived(book);
        }
        mListenerList.finishBroadcast();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBookList.add(new Book("1", "Android", "作者1"));
        mBookList.add(new Book("2", "IOS", "作者2"));
        new Thread(new ServiceWorker()).start();
    }

    @Override
    public void onDestroy() {
        mIsServiceDestroyed.set(true);
        super.onDestroy();
    }

    int num = 0;
    private class ServiceWorker implements Runnable {
        @Override public void run() {
            while (!mIsServiceDestroyed.get()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                num++;
                if (num == 5) {
                    mIsServiceDestroyed.set(true);
                }
                mHandler.post(task); // 向Handler发送消息,更新UI
            }
        }
    }

    private Handler mHandler = new Handler();

    private Runnable task = new Runnable() {
        @Override
        public void run() {
            int bookId = 1 + mBookList.size();
            Book newBook = new Book(""+bookId, "新书#" + bookId, "作者"+bookId);
            try {
                onNewBookArrived(newBook);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
