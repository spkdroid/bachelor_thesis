package hochschule.de.bachelorthesis.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import hochschule.de.bachelorthesis.room.tables.Food;
import hochschule.de.bachelorthesis.room.tables.UserHistory;

@Dao
public interface UserHistoryDao {
    @Insert
    void insert(UserHistory us);

    @Update
    void update(UserHistory us);

    @Delete
    void delete(UserHistory us);

    // debug only
    @Query("DELETE FROM food_table")
    void deleteAllUserHistories();

    @Query("SELECT * FROM user_history_table WHERE id=:id")
    LiveData<UserHistory> getById(int id);

    @Query("SELECT * FROM user_history_table ORDER BY id DESC LIMIT 1")
    LiveData<UserHistory> getLatest();
}
