package com.example.lockin.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.lockin.data.local.Converters;
import com.example.lockin.data.local.entity.LockSessionEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class LockSessionDao_Impl implements LockSessionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<LockSessionEntity> __insertionAdapterOfLockSessionEntity;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<LockSessionEntity> __updateAdapterOfLockSessionEntity;

  public LockSessionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfLockSessionEntity = new EntityInsertionAdapter<LockSessionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `lock_sessions` (`id`,`startTime`,`endTime`,`duration`,`status`,`createdAt`,`blockedApps`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final LockSessionEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getStartTime());
        statement.bindLong(3, entity.getEndTime());
        statement.bindLong(4, entity.getDuration());
        statement.bindString(5, entity.getStatus());
        statement.bindLong(6, entity.getCreatedAt());
        final String _tmp = __converters.fromStringList(entity.getBlockedApps());
        statement.bindString(7, _tmp);
      }
    };
    this.__updateAdapterOfLockSessionEntity = new EntityDeletionOrUpdateAdapter<LockSessionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `lock_sessions` SET `id` = ?,`startTime` = ?,`endTime` = ?,`duration` = ?,`status` = ?,`createdAt` = ?,`blockedApps` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final LockSessionEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getStartTime());
        statement.bindLong(3, entity.getEndTime());
        statement.bindLong(4, entity.getDuration());
        statement.bindString(5, entity.getStatus());
        statement.bindLong(6, entity.getCreatedAt());
        final String _tmp = __converters.fromStringList(entity.getBlockedApps());
        statement.bindString(7, _tmp);
        statement.bindLong(8, entity.getId());
      }
    };
  }

  @Override
  public Object insertSession(final LockSessionEntity session,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfLockSessionEntity.insertAndReturnId(session);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateSession(final LockSessionEntity session,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfLockSessionEntity.handle(session);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<LockSessionEntity> getActiveSession() {
    final String _sql = "SELECT * FROM lock_sessions WHERE status = 'ACTIVE' ORDER BY createdAt DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"lock_sessions"}, new Callable<LockSessionEntity>() {
      @Override
      @Nullable
      public LockSessionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "startTime");
          final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "endTime");
          final int _cursorIndexOfDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "duration");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfBlockedApps = CursorUtil.getColumnIndexOrThrow(_cursor, "blockedApps");
          final LockSessionEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final long _tmpStartTime;
            _tmpStartTime = _cursor.getLong(_cursorIndexOfStartTime);
            final long _tmpEndTime;
            _tmpEndTime = _cursor.getLong(_cursorIndexOfEndTime);
            final long _tmpDuration;
            _tmpDuration = _cursor.getLong(_cursorIndexOfDuration);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final List<String> _tmpBlockedApps;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfBlockedApps);
            _tmpBlockedApps = __converters.toStringList(_tmp);
            _result = new LockSessionEntity(_tmpId,_tmpStartTime,_tmpEndTime,_tmpDuration,_tmpStatus,_tmpCreatedAt,_tmpBlockedApps);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getActiveSessionSync(final Continuation<? super LockSessionEntity> $completion) {
    final String _sql = "SELECT * FROM lock_sessions WHERE status = 'ACTIVE' ORDER BY createdAt DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<LockSessionEntity>() {
      @Override
      @Nullable
      public LockSessionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "startTime");
          final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "endTime");
          final int _cursorIndexOfDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "duration");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfBlockedApps = CursorUtil.getColumnIndexOrThrow(_cursor, "blockedApps");
          final LockSessionEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final long _tmpStartTime;
            _tmpStartTime = _cursor.getLong(_cursorIndexOfStartTime);
            final long _tmpEndTime;
            _tmpEndTime = _cursor.getLong(_cursorIndexOfEndTime);
            final long _tmpDuration;
            _tmpDuration = _cursor.getLong(_cursorIndexOfDuration);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final List<String> _tmpBlockedApps;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfBlockedApps);
            _tmpBlockedApps = __converters.toStringList(_tmp);
            _result = new LockSessionEntity(_tmpId,_tmpStartTime,_tmpEndTime,_tmpDuration,_tmpStatus,_tmpCreatedAt,_tmpBlockedApps);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<LockSessionEntity>> getAllSessions() {
    final String _sql = "SELECT * FROM lock_sessions ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"lock_sessions"}, new Callable<List<LockSessionEntity>>() {
      @Override
      @NonNull
      public List<LockSessionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "startTime");
          final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "endTime");
          final int _cursorIndexOfDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "duration");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfBlockedApps = CursorUtil.getColumnIndexOrThrow(_cursor, "blockedApps");
          final List<LockSessionEntity> _result = new ArrayList<LockSessionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final LockSessionEntity _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final long _tmpStartTime;
            _tmpStartTime = _cursor.getLong(_cursorIndexOfStartTime);
            final long _tmpEndTime;
            _tmpEndTime = _cursor.getLong(_cursorIndexOfEndTime);
            final long _tmpDuration;
            _tmpDuration = _cursor.getLong(_cursorIndexOfDuration);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final List<String> _tmpBlockedApps;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfBlockedApps);
            _tmpBlockedApps = __converters.toStringList(_tmp);
            _item = new LockSessionEntity(_tmpId,_tmpStartTime,_tmpEndTime,_tmpDuration,_tmpStatus,_tmpCreatedAt,_tmpBlockedApps);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
