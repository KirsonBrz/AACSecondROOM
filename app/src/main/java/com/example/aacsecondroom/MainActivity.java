package com.example.aacsecondroom;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aacsecondroom.Data.DatabaseHandler;
import com.example.aacsecondroom.Model.Car;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private CarsAdapter carsAdapter;
    private ArrayList<Car> cars = new ArrayList<>();
    private RecyclerView recyclerView;
    private DatabaseHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        dbHandler = new DatabaseHandler(this);

        cars.addAll(dbHandler.getAllCars());

        carsAdapter = new CarsAdapter(this, cars, MainActivity.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(carsAdapter);


        FloatingActionButton floatingActionButton =
                (FloatingActionButton) findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAndEditCars(false, null, -1);
            }


        });


    }

    public void addAndEditCars(final boolean isUpdate, final Car car, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.layout_add_car, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);

        TextView newCarTitle = view.findViewById(R.id.newCarTitle);
        final EditText nameEditText = view.findViewById(R.id.nameEditText);
        final EditText priceEditText = view.findViewById(R.id.priceEditText);

        newCarTitle.setText(!isUpdate ? "Add Car" : "Edit Car");

        if (isUpdate && car != null) {
            nameEditText.setText(car.getName());
            priceEditText.setText(car.getPrice());
        }

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(isUpdate ? "Update" : "Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton(isUpdate ? "Delete" : "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {

                                if (isUpdate) {

                                    deleteCar(car, position);
                                } else {

                                    dialogBox.cancel();

                                }

                            }
                        });


        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(nameEditText.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter car name!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(priceEditText.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter car price!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }


                if (isUpdate && car != null) {

                    updateCar(nameEditText.getText().toString(), priceEditText.getText().toString(), position);
                } else {

                    createCar(nameEditText.getText().toString(), priceEditText.getText().toString());
                }
            }
        });
    }

    private void deleteCar(Car car, int position) {

        cars.remove(position);
        dbHandler.deleteCar(car);
        carsAdapter.notifyDataSetChanged();

    }

    private void updateCar(String name, String price, int position) {

        Car car = cars.get(position);

        car.setName(name);
        car.setPrice(price);

        dbHandler.updateCar(car);

        cars.set(position, car);

        carsAdapter.notifyDataSetChanged();


    }

    private void createCar(String name, String price) {

        long id = dbHandler.insertCar(name, price);


        Car car = dbHandler.getCar(id);

        if (car != null) {

            cars.add(0, car);
            carsAdapter.notifyDataSetChanged();

        }

    }
}