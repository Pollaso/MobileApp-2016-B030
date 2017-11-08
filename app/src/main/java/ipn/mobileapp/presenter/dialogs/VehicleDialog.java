package ipn.mobileapp.presenter.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.Map;

import ipn.mobileapp.R;
import ipn.mobileapp.model.enums.Crud;
import ipn.mobileapp.model.enums.RequestType;
import ipn.mobileapp.model.enums.Servlets;
import ipn.mobileapp.model.pojo.Device;
import ipn.mobileapp.model.pojo.Vehicle;
import ipn.mobileapp.model.service.OkHttpServletRequest;
import ipn.mobileapp.model.utility.JsonUtils;
import ipn.mobileapp.presenter.validation.TextValidator;
import ipn.mobileapp.presenter.validation.Validator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class VehicleDialog implements View.OnClickListener {
    private Context context;
    private AlertDialog dialog;
    private DialogInterface.OnDismissListener dismissListener;

    private EditText etLicensePlate;
    private EditText etSerialKey;
    private TextView tvMsjVehicle;

    private Button btnSave;
    private Button btnCancel;

    private Crud mode;
    private boolean udMode;
    private Vehicle vehicle;

    public VehicleDialog(Context context) {
        this.context = context;
    }

    public VehicleDialog(Context context, Vehicle vehicle, DialogInterface.OnDismissListener dismissListener, Crud mode) {
        this.context = context;
        this.vehicle = vehicle;
        this.dismissListener = dismissListener;
        this.mode = mode;
        this.udMode = (mode != Crud.CREATE);
        this.vehicle = udMode ? vehicle : new Vehicle();
    }

    @Override
    public void onClick(View v) {
        createDialog();
        getComponents();
        setComponentAttributes();
    }

    private void createDialog() {
        String title = context.getString(R.string.title_dialog_add_vehicle);

        if (udMode)
            title = mode == Crud.UPDATE ? context.getString(R.string.title_dialog_edit_vehicle) : context.getString(R.string.title_dialog_delete_vehicle);

        dialog = new AlertDialog.Builder(context)
                .setView(R.layout.dialog_vehicle)
                .setTitle(title)
                .setCancelable(true)
                .setOnDismissListener(dismissListener)
                .create();
        dialog.show();
    }

    private void getComponents() {
        btnSave = (Button) dialog.findViewById(R.id.btn_save_vehicle);
        btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
        tvMsjVehicle = (TextView) dialog.findViewById(R.id.tv_message);

        etLicensePlate = (EditText) dialog.findViewById(R.id.et_license_plate);
        etSerialKey = (EditText) dialog.findViewById(R.id.et_serial_key);
    }

    private void setComponentAttributes() {
        String saveBtnText = context.getString(R.string.btn_save_vehicle);

        if (udMode)
            saveBtnText = (mode == Crud.UPDATE) ? context.getString(R.string.btn_save_changes) : context.getString(R.string.btn_delete);

        btnSave.setText(saveBtnText);
        btnSave.setOnClickListener(save);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        tvMsjVehicle.setText("¿Desea eliminar el vehículo con la matrícula automovilística " + vehicle.getLicensePlate() + "?");
        tvMsjVehicle.setVisibility(mode == Crud.DELETE ? View.VISIBLE : View.GONE);

        if (udMode && mode == Crud.UPDATE) {
            etLicensePlate.setText(vehicle.getLicensePlate());
            etSerialKey.setText(vehicle.getDevice().getSerialKey());
        }

        final TextView[] fields = new TextView[]{etLicensePlate, etSerialKey};
        if (mode != Crud.DELETE) {
            final Validator validator = new Validator(context);

            etLicensePlate.addTextChangedListener(new TextValidator(etLicensePlate) {
                @Override
                public void validate(TextView textView, String text) {
                    if (!validator.isValidCarPlates(text))
                        etLicensePlate.setError(context.getString(R.string.warning_car_plates));
                    else {
                        vehicle.setLicensePlate(text);
                        btnSave.setEnabled(validator.validateFields(fields));
                    }
                }
            });
            etSerialKey.addTextChangedListener(new TextValidator(etSerialKey) {
                @Override
                public void validate(TextView textView, String text) {
                    if (!validator.isValidUUID(text))
                        etSerialKey.setError(context.getString(R.string.warning_serial_key));
                    else {
                        Device device = new Device();
                        device.setSerialKey(text);
                        vehicle.setDevice(device);
                        btnSave.setEnabled(validator.validateFields(fields));
                    }
                }
            });
        } else {
            for (TextView field : fields)
                field.setVisibility(View.GONE);
        }
    }

    private void processResults(final String response) {
        dialog.dismiss();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (response != null && JsonUtils.isValidJson(response)) {
                    JsonObject json = (JsonObject) new JsonParser().parse(response);
                    if (json.has("data")) {
                        //Vehicle vehicle = new Gson().fromJson(json.get("data").getAsString(), Vehicle.class);
                        String message = context.getString(R.string.msj_vehicle_added);
                        if (udMode)
                            message = mode == Crud.UPDATE ? context.getString(R.string.msj_vehicle_saved) : context.getString(R.string.msj_vehicle_deleted);
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    } else if (json.has("warnings")) {
                        JsonObject warnings = json.getAsJsonObject("warnings");
                        if (warnings.has("noneInserted") || warnings.has("notAllInserted"))
                            Toast.makeText(context, context.getString(R.string.warning_vehicle_not_inserted), Toast.LENGTH_SHORT).show();
                    }
                } else
                    Toast.makeText(context, context.getString(R.string.error_server), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Button.OnClickListener save = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!udMode) {
                SharedPreferences sharedPreferences = context.getSharedPreferences("currentUser", MODE_PRIVATE);
                vehicle.setOwner(sharedPreferences.getString("id", null));
            }

            if (mode != Crud.DELETE) {
                final TextView[] fields = new TextView[]{etLicensePlate, etSerialKey};
                final Validator validator = new Validator(context);
                if (!validator.validateFields(fields))
                    return;
            }

            Map<String, String> params = new ArrayMap<>();
            if (mode == Crud.DELETE) {
                params.put("id", vehicle.getId());
                params.put("ownerId", vehicle.getOwner());
            } else
                params.put("vehicle", vehicle.toString());

            OkHttpServletRequest request = new OkHttpServletRequest(context);
            RequestType type = RequestType.POST;
            if (udMode)
                type = (mode == Crud.UPDATE) ? RequestType.PUT : RequestType.DELETE;
            Request builtRequest = request.buildRequest(Servlets.VEHICLE, type, params);
            OkHttpClient client = request.buildClient();
            client.newCall(builtRequest).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    processResults(null);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    processResults(response.body().string());
                }
            });
        }
    };
}
