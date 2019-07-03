package hochschule.de.bachelorthesis.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import hochschule.de.bachelorthesis.model.FoodAddModel;
import hochschule.de.bachelorthesis.model.FoodDataModel;
import hochschule.de.bachelorthesis.model.FoodOverviewModel;
import hochschule.de.bachelorthesis.model.MeasurementAddModel;
import hochschule.de.bachelorthesis.model.MeasurementModel;
import hochschule.de.bachelorthesis.model.Repository;
import hochschule.de.bachelorthesis.room.tables.Food;
import hochschule.de.bachelorthesis.room.tables.Measurement;
import hochschule.de.bachelorthesis.room.tables.UserHistory;
import java.util.List;

public class FoodViewModel extends AndroidViewModel {

  // Database
  private Repository mRepository;
  private LiveData<List<Food>> mAllFoods;
  private LiveData<UserHistory> mUserHistoryLatest;

  // Models
  private FoodAddModel mFoodAddModel;
  private FoodOverviewModel mFoodOverviewModel;
  private FoodDataModel mFoodDataModel;
  private MeasurementModel mMeasurementModel; // Also used for measurement edit fragment
  private MeasurementAddModel mMeasurementAddModel;


  public FoodViewModel(@NonNull Application application) {
    super(application);

    mRepository = new Repository(application);
    mAllFoods = mRepository.getAllFoods();
    mUserHistoryLatest = mRepository.getUserHistoryLatest();

    mFoodDataModel = new FoodDataModel();
    mFoodAddModel = new FoodAddModel();
    mFoodOverviewModel = new FoodOverviewModel();

    mMeasurementModel = new MeasurementModel();
    mMeasurementAddModel = new MeasurementAddModel();
  }

  /* LOAD FUNCTIONS */
  public void loadOverviewFragment(Food food) {
    updateFoodOverviewModel(food);
  }

  public void loadDataFragment(Food food) {
    updateFoodDataModel(food);
  }

  public void loadMeasurementFragment(Measurement measurement) {
    updateMeasurementModel(measurement);
  }

  /* UPDATE MODELS */

  /**
   * This method will update the food overview model.
   */
  private void updateFoodOverviewModel(Food food) {
    if (food == null) {
      return;
    }

    mFoodOverviewModel.setFoodName(food.getFoodName());
    mFoodOverviewModel.setBrandName(food.getBrandName());
    mFoodOverviewModel.setType(food.getFoodType());
    mFoodOverviewModel.setKiloCalories(food.getKiloCalories());
  }

  /**
   * This method will update the food add model.
   */
  public void updateFoodAddModeL(Food food) {
    if (food == null) {
      return;
    }

    mFoodAddModel.setFoodName(food.getFoodName());
    mFoodAddModel.setBrandName(food.getBrandName());
    mFoodAddModel.setType(food.getFoodType());
    mFoodAddModel.setKiloCalories(food.getKiloCalories());
    mFoodAddModel.setKiloJoules(food.getKiloJoules());
    mFoodAddModel.setFat(food.getFat());
    mFoodAddModel.setSaturates(food.getSaturates());
    mFoodAddModel.setProtein(food.getProtein());
    mFoodAddModel.setCarbohydrates(food.getCarbohydrate());
    mFoodAddModel.setSugars(food.getSugars());
    mFoodAddModel.setSalt(food.getSalt());
  }

  /**
   * This method will update the food data model.
   */
  private void updateFoodDataModel(Food food) {
    if (food == null) {
      return;
    }

    mFoodDataModel.setFoodName(food.getFoodName());
    mFoodDataModel.setBrandName(food.getBrandName());
    mFoodDataModel.setType(food.getFoodType());
    mFoodDataModel.setKiloCalories(food.getKiloCalories());
    mFoodDataModel.setKiloJoules(food.getKiloJoules());
    mFoodDataModel.setFat(food.getFat());
    mFoodDataModel.setSaturates(food.getSaturates());
    mFoodDataModel.setProtein(food.getProtein());
    mFoodDataModel.setCarbohydrates(food.getCarbohydrate());
    mFoodDataModel.setSugar(food.getSugars());
    mFoodDataModel.setSalt(food.getSalt());
  }

  private void updateMeasurementModel(Measurement measurement) {
    if (measurement == null) {
      return;
    }

    mMeasurementModel.setGi(measurement.isGi());
    mMeasurementModel.setTimestamp(measurement.getTimeStamp());
    mMeasurementModel.setAmount(measurement.getAmount());
    mMeasurementModel.setStressed(measurement.getStress());
    mMeasurementModel.setTired(measurement.getTired());
    mMeasurementModel.setPhysicallyActivity(measurement.isPhysicallyActivity());
    mMeasurementModel.setAlcoholConsumed(measurement.isAlcoholConsumed());
    mMeasurementModel.setIll(measurement.isIll());
    mMeasurementModel.setMedication(measurement.isMedication());
    mMeasurementModel.setPeriod(measurement.isPeriod());
    mMeasurementModel.setValue0(measurement.getGlucoseStart());
    mMeasurementModel.setValue15(measurement.getGlucose15());
    mMeasurementModel.setValue30(measurement.getGlucose30());
    mMeasurementModel.setValue45(measurement.getGlucose45());
    mMeasurementModel.setValue60(measurement.getGlucose60());
    mMeasurementModel.setValue75(measurement.getGlucose75());
    mMeasurementModel.setValue90(measurement.getGlucose90());
    mMeasurementModel.setValue105(measurement.getGlucose105());
    mMeasurementModel.setValue120(measurement.getGlucose120());
  }

  private void updateMeasurementAddModel(Measurement measurement) {
    if (measurement == null) {
      return;
    }

    mMeasurementAddModel.setGi(measurement.isGi());
    mMeasurementAddModel.setTimestamp(measurement.getTimeStamp());
    mMeasurementAddModel.setAmount(measurement.getAmount());
    mMeasurementAddModel.setStressed(measurement.getStress());
    mMeasurementAddModel.setTired(measurement.getTired());
    mMeasurementAddModel.setPhysicallyActivity(measurement.isPhysicallyActivity());
    mMeasurementAddModel.setAlcoholConsumed(measurement.isAlcoholConsumed());
    mMeasurementAddModel.setIll(measurement.isIll());
    mMeasurementAddModel.setMedication(measurement.isMedication());
    mMeasurementAddModel.setPeriod(measurement.isPeriod());
    mMeasurementAddModel.setValue0(measurement.getGlucoseStart());
  }

  /**
   * Inserts a food to the database.
   *
   * @param food - The food to insert to the database.
   */
  public void insertFood(Food food) {
    mRepository.insert(food);
  }

  /**
   * Updates an existing food in the database.
   *
   * @param food - The food to update.
   */
  public void update(Food food) {
    mRepository.update(food);
  }

  /**
   * Deletes a food in the database.
   *
   * @param food - The food to delete.
   */
  public void delete(Food food) {
    mRepository.delete(food);
  }

  /**
   * Deletes all foods in the database.
   */
  public void deleteAllFoods() {
    mRepository.deleteAllFood();
  }

  /**
   * Gets all foods from the database.
   *
   * @return - A Live data list with all foods.
   */
  public LiveData<List<Food>> getAllFoods() {
    return mAllFoods;
  }

  /**
   * Gets the food by ID.
   *
   * @param id - The food's id.
   * @return - A live data object with the food.
   */
  public LiveData<Food> getFoodById(int id) {
    return mRepository.getFoodById(id);
  }

  /* MEASUREMENT */

  public LiveData<Integer> getMeasurementAmountRows(int foodId) {
    return mRepository.getMeasurementAmountRowsByFoodId(foodId);
  }

  /**
   * Inserts a measurement to the database.
   *
   * @param measurement - The measurement to insert to the database.
   */
  public void insertMeasurement(Measurement measurement) {
    mRepository.insert(measurement);
  }

  public void deleteMeasurement(Measurement measurement) {
    mRepository.delete(measurement);
  }

  /**
   * Updates the measurement object in the table.
   *
   * @param measurement - Measurement object to update
   */
  public void updateMeasurement(Measurement measurement) {
    mRepository.insert(measurement);
  }

  /**
   * Gets all measurements from the food objects from the database.
   *
   * @param foodId - The Id of the food.
   * @return - A live data list of measurements of the food.
   */
  public LiveData<List<Measurement>> getAllMeasurementsById(int foodId) {
    return mRepository.getAllMeasurementsByFoodId(foodId);
  }

  public LiveData<Measurement> getMeasurementById(int id) {
    return mRepository.getMeasurementById(id);
  }

  /* USER HISTORY */

  /**
   * Gets the latest user history entry of the database.
   *
   * @return - A live data list of the latest user history
   */
  public LiveData<UserHistory> getUserHistoryLatest() {
    return mUserHistoryLatest;
  }

  public void deleteAllMeasurementFromFoodWithId(int foodId) {
    mRepository.deleteAllMeasurementsWithId(foodId);
  }

  /* GETTER */

  public FoodAddModel getFoodAddDataModel() {
    return mFoodAddModel;
  }

  public FoodOverviewModel getFoodInfoOverviewModel() {
    return mFoodOverviewModel;
  }

  public FoodDataModel getFoodInfoDataModel() {
    return mFoodDataModel;
  }

  public MeasurementModel getMeasurementModel() {
    return mMeasurementModel;
  }

  public MeasurementAddModel getMeasurementAddModel() {
    return mMeasurementAddModel;
  }

}
