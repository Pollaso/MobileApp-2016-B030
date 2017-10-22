package ipn.mobileapp.presenter.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import ipn.mobileapp.R;
import ipn.mobileapp.model.enums.Crud;
import ipn.mobileapp.model.enums.RequestType;
import ipn.mobileapp.model.enums.Servlets;
import ipn.mobileapp.model.pojo.Vehicle;
import ipn.mobileapp.model.service.OkHttpServletRequest;
import ipn.mobileapp.model.service.SharedPreferencesManager;
import ipn.mobileapp.model.utility.JsonUtils;
import ipn.mobileapp.presenter.dialogs.VehicleDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class VehicleAdapter extends ArrayAdapter<Vehicle> {
    private Context context;
    private int resource;
    private DialogInterface.OnDismissListener dismissListener;

    public VehicleAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Vehicle> objects, DialogInterface.OnDismissListener dismissListener) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.dismissListener = dismissListener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Vehicle vehicle = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final String id = (String) new SharedPreferencesManager(context, context.getString(R.string.current_user_filename)).getValue("id", String.class);
                String idUser = vehicle.getUserData() != null ? vehicle.getUserData().getId() : null;
                final boolean isUser = id.equals(idUser);

                if (vehicle.getUserData() != null && vehicle.getUserData().getId() != null && !isUser) {
                    Toast.makeText(context, context.getString(R.string.warning_vehicle_has_user), Toast.LENGTH_LONG).show();
                    return true;
                }

                final AlertDialog dialog = new AlertDialog.Builder(context)
                        .setView(R.layout.dialog_vehicle)
                        .setTitle(R.string.title_dialog_set_vehicle_user)
                        .setCancelable(true)
                        .setOnDismissListener(dismissListener)
                        .create();
                dialog.show();

                TextView tvMessage = (TextView) dialog.findViewById(R.id.tv_message);
                String message = isUser ? context.getString(R.string.tv_remove_vehicle_user) : context.getString(R.string.tv_set_vehicle_user);
                tvMessage.setText(message);
                tvMessage.setVisibility(View.VISIBLE);

                dialog.findViewById(R.id.et_license_plate).setVisibility(View.GONE);
                dialog.findViewById(R.id.et_serial_key).setVisibility(View.GONE);

                Button btnSetUser = (Button) dialog.findViewById(R.id.btn_save_vehicle);
                btnSetUser.setText(context.getString(R.string.btn_accept));
                btnSetUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Map<String, String> params = new ArrayMap<>();

                        vehicle.setOwner(vehicle.getOwnerData().getId());
                        if (isUser)
                            vehicle.setUser(null);
                        else
                            vehicle.setUser(id);

                        params.put("vehicle", vehicle.toString());

                        final OkHttpServletRequest request = new OkHttpServletRequest(context);
                        Request builtRequest = request.buildRequest(Servlets.VEHICLE, RequestType.PUT, params);
                        OkHttpClient client = request.buildClient();
                        client.newCall(builtRequest).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                dialog.dismiss();
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, context.getString(R.string.error_server), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                dialog.dismiss();
                                final String jsonResponse = response.body().string();
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        String message = context.getString(R.string.error_server);
                                        if (jsonResponse != null && JsonUtils.isValidJson(jsonResponse)) {
                                            JsonObject json = new JsonParser().parse(jsonResponse).getAsJsonObject();
                                            if (json.has("data"))
                                                message = context.getString(R.string.msj_vehicle_user_updated);
                                            else if (json.has("warnings"))
                                                message = context.getString(R.string.warning_vehicle_user_not_updated);
                                        }
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                });

                Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                return true;
            }
        });

        ImageButton imgBtnEdit = (ImageButton) convertView.findViewById(R.id.img_btn_edit);
        imgBtnEdit.setOnClickListener(new VehicleDialog(context, vehicle, dismissListener, Crud.UPDATE));

        ImageButton imgBtnDelete = (ImageButton) convertView.findViewById(R.id.img_btn_delete);
        imgBtnDelete.setOnClickListener(new VehicleDialog(context, vehicle, dismissListener, Crud.DELETE));

        TextView tvVin = (TextView) convertView.findViewById(R.id.tv_vin);

        TextView tvRegistrationPlates = (TextView) convertView.findViewById(R.id.tv_license_plate);
        tvRegistrationPlates.setText(vehicle.getLicensePlate());

        TextView tvSerialKey = (TextView) convertView.findViewById(R.id.tv_serial_key);
        tvSerialKey.setText(vehicle.getDevice().getSerialKey());

        TextView tvOwnerName = (TextView) convertView.findViewById(R.id.tv_owner);
        tvOwnerName.setText(vehicle.getOwnerData().getName());

        TextView tvUserName = (TextView) convertView.findViewById(R.id.tv_user);
        if (vehicle.getUserData() != null && vehicle.getUserData().getId() != null)
            tvUserName.setText(vehicle.getUserData().getName());

        return convertView;
    }
}
