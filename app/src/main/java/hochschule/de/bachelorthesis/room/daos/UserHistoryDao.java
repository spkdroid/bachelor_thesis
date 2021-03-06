package hochschule.de.bachelorthesis.room.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import hochschule.de.bachelorthesis.room.tables.UserHistory;

/**
 * @author Maik Thielen
 * <p>
 * DaO class for the UserHistory table
 * <p>
 * Contains functions to wrap SQL functions in.
 */
@Dao
public interface UserHistoryDao {

    @Insert
    void insert(UserHistory uh);

    @Update
    void update(UserHistory uh);

    @Delete
    void delete(UserHistory uh);

    // debug only
    @Query("DELETE FROM user_history_table")
    void deleteAllUserHistories();

    @Query("SELECT * FROM user_history_table WHERE id=:id")
    LiveData<UserHistory> getById(int id);

    @Query("SELECT * FROM user_history_table ORDER BY id DESC LIMIT 1")
    LiveData<UserHistory> getLatest();
}
