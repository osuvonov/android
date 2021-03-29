package org.telegram.irooms.company;

import android.app.Application;

import org.telegram.irooms.database.Company;
import org.telegram.irooms.task.RoomsRepository;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class CompanyViewModel extends AndroidViewModel {

    private RoomsRepository roomsRepository;


    public CompanyViewModel(@NonNull Application application) {
        super(application);
        roomsRepository = RoomsRepository.getInstance(application);
     }
    public void insert(Company company) {
        roomsRepository.insert(company);
    }

}
