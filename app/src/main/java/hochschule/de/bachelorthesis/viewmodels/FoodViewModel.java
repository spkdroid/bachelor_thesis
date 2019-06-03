package hochschule.de.bachelorthesis.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import hochschule.de.bachelorthesis.model.MeasurementAddModel;
import hochschule.de.bachelorthesis.model.MeasurementCurrentModel;
import hochschule.de.bachelorthesis.model.FoodAddDataModel;
import hochschule.de.bachelorthesis.model.FoodInfoDataModel;
import hochschule.de.bachelorthesis.model.FoodInfoOverviewModel;
import hochschule.de.bachelorthesis.model.Repository;
import hochschule.de.bachelorthesis.room.tables.Food;
import hochschule.de.bachelorthesis.room.tables.Measurement;
import hochschule.de.bachelorthesis.room.tables.UserHistory;

public class FoodViewModel extends AndroidViewModel {

  // Database
  private Repository mRepository;
  private LiveData<List<Food>> mAllFoods;
  private LiveData<List<Measurement>> mAllMeasurements;
  private LiveData<UserHistory> mUserHistoryLatest;

  // Models
  private FoodAddDataModel mFoodAddDataModel;
  private FoodInfoOverviewModel mFoodInfoOverviewModel;
  private FoodInfoDataModel mFoodInfoDataModel;
  private MeasurementAddModel mMeasurementAddModel;
  private MeasurementCurrentModel mMeasurementModel;

  // Others
  private LifecycleOwner mLco;


  public FoodViewModel(@NonNull Application application) {
    super(application);

    mRepository = new Repository(application);
    mAllFoods = mRepository.getAllFoods();
    mAllMeasurements = mRepository.getAllMeasurements();
    mUserHistoryLatest = mRepository.getUserHistoryLatest();

    mFoodAddDataModel = new FoodAddDataModel();
    mFoodInfoOverviewModel = new FoodInfoOverviewModel();
    mFoodInfoDataModel = new FoodInfoDataModel();
    mMeasurementAddModel = new MeasurementAddModel();
    mMeasurementModel = new MeasurementCurrentModel();
  }

  /* FOOD */

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

  /**
   * This function will load the current food from database and update the viewModel (overview and
   * data models). This will cause the UI to update itself due to the live data.
   *
   * @param foodId - ID of the food clicked
   * @param lco - Lifecycle owner
   */
  public void load(int foodId, LifecycleOwner lco) {
    final LiveData<Food> ldf = getFoodById(foodId);

    ldf.observe(lco, new Observer<Food>() {
      @Override
      public void onChanged(Food food) {
        ldf.removeObserver(this);
        if (food == null) {
          return;
        }

        //overview tab
        updateFoodOverviewModel(food.getFoodName(), food.getBrandName(), food.getFoodType(),
            food.getKiloCalories(), food.getAmountMeasurements(), food.getMaxGlucose(),
            food.getAverageGlucose(), food.getRating(), food.getPersonalIndex());

        //food data tab
        updateFoodDataModel(food.getFoodName(), food.getBrandName(), food.getFoodType(),
            food.getKiloCalories(), food.getKiloJoules(),
            food.getFat(), food.getSaturates(), food.getProtein(),
            food.getCarbohydrate(), food.getSugars(), food.getSalt());
      }
    });
  }

  /* MEASUREMENT */

  /**
   * Inserts a measurement to the database.
   *
   * @param measurement - The measurement to insert to the database.
   */
  public void insertMeasurement(Measurement measurement) {
    mRepository.insert(measurement);
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

  public void loadEditMeasurement(int measurementId, int foodId) {

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

  /**
   * DEBUG ONLY
   * <p>
   * inserts a test measurement to the table.
   *
   * @param lco - The lifecycle owner, needed for the observe function
   */
  public void addTemplateMeasurement(final LifecycleOwner lco, final int foodId) {
    final LiveData<UserHistory> uh = getUserHistoryLatest();
    uh.observe(lco, new Observer<UserHistory>() {
      @Override
      public void onChanged(UserHistory userHistory) {
        uh.removeObserver(this);

        if (userHistory == null) {
          return;
        }

        // Build timestamp
        Date date = new Date(); // current date and time

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy_HH:mm", Locale.getDefault());
        String timeStamp = sdf.format(date);

        insertMeasurement(
            new Measurement(foodId, userHistory.id, timeStamp, 100, "not stressed", "not tired",
                100));
      }
    });
  }

  public void deleteAllMeasurementFromFoodWithId(int foodId) {
    mRepository.deleteAllMeasurementsWithId(foodId);
  }

  /* UPDATE MODELS */

  /**
   * This method will update the food add model.
   *
   * @param foodName - Name of the food.
   * @param brandName - Brand of the food.
   * @param foodType - Type of the food (e.g. fruit, snacks, drinks, ...).
   * @param kiloCalories - Kilo calories (kCal) of the food.
   * @param kiloJoules - Kilo joules (kJ) of the food.
   * @param fat - Fat of the food.
   * @param saturates - Saturates of the food.
   * @param protein - Proteins of the food.
   * @param carbohydrates - Carbohydrates of the food.
   * @param sugars - Sugar of the food.
   * @param salt - Salt of the food.
   */
  public void updateFoodAddOverviewModeL(String foodName, String brandName, String foodType,
      float kiloCalories, float kiloJoules, float fat, float saturates, float protein,
      float carbohydrates, float sugars, float salt) {
    mFoodAddDataModel.setFoodName(foodName);
    mFoodAddDataModel.setBrandName(brandName);
    mFoodAddDataModel.setType(foodType);
    mFoodAddDataModel.setKiloCalories(kiloCalories);
    mFoodAddDataModel.setKiloJoules(kiloJoules);
    mFoodAddDataModel.setFat(fat);
    mFoodAddDataModel.setSaturates(saturates);
    mFoodAddDataModel.setProtein(protein);
    mFoodAddDataModel.setCarbohydrates(carbohydrates);
    mFoodAddDataModel.setSugars(sugars);
    mFoodAddDataModel.setSalt(salt);
  }

  /**
   * This method will update the food overview model.
   *
   * @param foodName - Name of the food.
   * @param brandName - Brand of the food.
   * @param foodType - Type of the food (e.g. fruit, snacks, drinks, ...).
   * @param kiloCalories - Kilo calories (kCal) of the food.
   * @param measurementsAmount - Amount of measurements done so far.
   * @param glucoseMax - Max Glucose outcome so far.
   * @param glucoseAverage - Average Glucose so far.
   * @param rating - Rating for that food
   * @param personalIndex - Personal index
   */
  private void updateFoodOverviewModel(String foodName, String brandName, String foodType,
      Float kiloCalories, int measurementsAmount,
      int glucoseMax, int glucoseAverage,
      String rating, int personalIndex) {
    mFoodInfoOverviewModel.setFoodName(foodName);
    mFoodInfoOverviewModel.setBrandName(brandName);
    mFoodInfoOverviewModel.setType(foodType);
    mFoodInfoOverviewModel.setKiloCalories(kiloCalories);

    mFoodInfoOverviewModel.setMeasurementsAmount(measurementsAmount);
    mFoodInfoOverviewModel.setMaxGlucose(glucoseMax);
    mFoodInfoOverviewModel.setAverageGlucose(glucoseAverage);
    mFoodInfoOverviewModel.setRating(rating);
    mFoodInfoOverviewModel.setPersonalIndex(personalIndex);
  }

  /**
   * This method will update the food data model.
   *
   * @param foodName - Name of the food.
   * @param brandName - Brand of the food.
   * @param foodType - Type of the food (e.g. fruit, snacks, drinks, ...).
   * @param kiloCalories - Kilo calories (kCal) of the food.
   * @param kiloJoules - Kilo joules (kJ) of the food.
   * @param fat - Fat of the food.
   * @param saturates - Saturates of the food.
   * @param protein - Proteins of the food.
   * @param carbohydrates - Carbohydrates of the food.
   * @param sugars - Sugar of the food.
   * @param salt - Salt of the food.
   */
  private void updateFoodDataModel(String foodName, String brandName, String foodType,
      float kiloCalories, float kiloJoules,
      float fat, float saturates,
      float protein, float carbohydrates,
      float sugars, float salt) {
    mFoodInfoDataModel.setFoodName(foodName);
    mFoodInfoDataModel.setBrandName(brandName);
    mFoodInfoDataModel.setType(foodType);
    mFoodInfoDataModel.setKiloCalories(kiloCalories);
    mFoodInfoDataModel.setKiloJoules(kiloJoules);
    mFoodInfoDataModel.setFat(fat);
    mFoodInfoDataModel.setSaturates(saturates);
    mFoodInfoDataModel.setProtein(protein);
    mFoodInfoDataModel.setCarbohydrates(carbohydrates);
    mFoodInfoDataModel.setSugar(sugars);
    mFoodInfoDataModel.setSalt(salt);
  }


  /* GETTER */

  public FoodAddDataModel getFoodAddDataModel() {
    return mFoodAddDataModel;
  }

  public FoodInfoOverviewModel getFoodInfoOverviewModel() {
    return mFoodInfoOverviewModel;
  }

  public FoodInfoDataModel getFoodInfoDataModel() {
    return mFoodInfoDataModel;
  }

  public MeasurementAddModel getMeasurementAddModel() {
    return mMeasurementAddModel;
  }

  public MeasurementCurrentModel getMeasurementCurrentModel() {
    return mMeasurementModel;
  }
}
