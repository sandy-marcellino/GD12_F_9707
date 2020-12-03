package com.example.unguided8_xxxx;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditUserActivity extends AppCompatActivity {
    private ImageButton ibBack;
    private EditText etNama, etNim, etUsername, etPassword;
    private AutoCompleteTextView exposedDropdownFakultas, exposedDropdownProdi;
    private RadioGroup rgJenisKelamin;
    private RadioButton rbLaki, rbPerempuan;
    private MaterialButton btnCancel, btnUpdate;
    private String sIdUser, sName, sNim, sProdi, sFakultas, sJenisKelamin;
    private String[] saProdi = new String[] {"Informatika", "Manajemen", "Ilmu Komunikasi", "Ilmu Hukum"};
    private String[] saFakultas = new String[] {"FTI", "FBE", "FISIP", "FH"};
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        progressDialog = new ProgressDialog(this);

        ibBack = findViewById(R.id.ibBack);
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        etNama = findViewById(R.id.etNama);
        etNim = findViewById(R.id.etNim);
        exposedDropdownProdi = findViewById(R.id.edProdi);
        exposedDropdownFakultas = findViewById(R.id.edFakultas);
        rgJenisKelamin = findViewById(R.id.rgJenisKelamin);
        rbLaki = findViewById(R.id.rbLakiLaki);
        rbPerempuan = findViewById(R.id.rbPerempuan);
        etPassword = findViewById(R.id.etPassword);
        btnCancel = findViewById(R.id.btnCancel);
        btnUpdate = findViewById(R.id.btnUpdate);

        Intent i = getIntent();
        sIdUser = i.getStringExtra("id");
        sName = i.getStringExtra("name");
        sNim = i.getStringExtra("nim");
        sProdi = i.getStringExtra("prodi");
        sFakultas = i.getStringExtra("fakultas");
        sJenisKelamin = i.getStringExtra("jenis_kelamin");

        etNama.setText(sName);
        etNim.setText(sNim);
        exposedDropdownProdi.setText(sProdi);
        exposedDropdownFakultas.setText(sFakultas);

        ArrayAdapter<String> adapterProdi = new ArrayAdapter<>(Objects.requireNonNull(this), R.layout.list_item,
                R.id.item_list, saProdi);
        exposedDropdownProdi.setAdapter(adapterProdi);

        exposedDropdownProdi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                sProdi = saProdi[i];
            }
        });

        ArrayAdapter<String> adapterFakultas = new ArrayAdapter<>(Objects.requireNonNull(this), R.layout.list_item, R.id.item_list, saFakultas);
        exposedDropdownFakultas.setAdapter(adapterFakultas);


        exposedDropdownFakultas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                sFakultas = saFakultas[i];
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        runOnUiThread(new Runnable() {
            public void run() {
                if (sJenisKelamin.equalsIgnoreCase("laki-laki")) {
                    rbLaki.setChecked(true);
                }
                else {
                    rbPerempuan.setChecked(true);
                }
            }
        });
        rgJenisKelamin.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.rbLakiLaki:
                        sJenisKelamin = "Laki-Laki";
                        break;
                    case R.id.rbPerempuan:
                        sJenisKelamin = "Perempuan";
                        break;
                }
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etNama.getText().toString().isEmpty())
                {
                    etNama.setError("Isikan dengan benar");
                    etNama.requestFocus();
                }
                else if(etNim.getText().toString().isEmpty())
                {
                    etNim.setError("Isikan dengan benar");
                    etNim.requestFocus();
                }
                else if(sProdi.isEmpty())
                {
                    exposedDropdownProdi.setError("Isikan dengan benar", null);
                    exposedDropdownProdi.requestFocus();
                }
                else if(sFakultas.isEmpty())
                {
                    exposedDropdownFakultas.setError("Isikan dengan benar", null);
                    exposedDropdownFakultas.requestFocus();
                }
                else if(etPassword.getText().toString().isEmpty())
                {
                    etPassword.setError("Isikan dengan benar");
                    etPassword.requestFocus();
                }
                else
                {
                    progressDialog.show();
                    saveUser();
                }
            }
        });
    }

    private void saveUser() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<UserResponse> add = apiService.updateUser(sIdUser, etNama.getText().toString(), sProdi, sFakultas, sJenisKelamin,
                etPassword.getText().toString());

        add.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                Toast.makeText(EditUserActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                Intent i = new Intent(EditUserActivity.this, ShowListUserActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(EditUserActivity.this, "Gagal mengupdate user", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }
}