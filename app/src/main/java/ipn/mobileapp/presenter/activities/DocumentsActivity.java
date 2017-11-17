package ipn.mobileapp.presenter.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import ipn.mobileapp.R;
import ipn.mobileapp.model.enums.RequestType;
import ipn.mobileapp.model.enums.Servlets;
import ipn.mobileapp.model.pojo.Document;
import ipn.mobileapp.model.service.OkHttpServletRequest;
import ipn.mobileapp.model.service.SharedPreferencesManager;
import ipn.mobileapp.model.utility.JsonUtils;
import ipn.mobileapp.presenter.validation.TextValidator;
import ipn.mobileapp.presenter.validation.Validator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DocumentsActivity extends BaseActivity {
    private static final int READ_REQUEST_CODE = 1337;

    private View contentView;

    private EditText etDocumentName;
    private TextView tvDocumentPath;

    private Button btnModifyDocument;
    private ImageButton imgBtnFindDocument;
    private Button btnUploadedDocument;

    private String id;
    private TextView[] fields;
    private Validator validator = new Validator(this);

    private Document document = null;

    private File file;
    private byte[] fileBytes;
    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        contentView = inflater.inflate(R.layout.activity_documents, null, false);
        drawer.addView(contentView, 0);

        SharedPreferencesManager manager = new SharedPreferencesManager(this, getString(R.string.current_user_filename));
        id = (String) manager.getValue("id", String.class);

        document = new Document();
        showProgressDialog();
        getDocument();
        getComponents();
        setComponentAttributes();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            fileUri = resultData.getData();

            String[] filePathColumn = {MediaStore.Files.FileColumns.SIZE};
            Cursor cursor = getContentResolver().query(fileUri, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            long size = cursor.getLong(columnIndex);
            cursor.close();

            file = new File(fileUri.getPath());
            fileBytes = new byte[(int) size];
            try {
                BufferedInputStream buf = new BufferedInputStream(getContentResolver().openInputStream(fileUri));
                buf.read(fileBytes, 0, fileBytes.length);
                buf.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            tvDocumentPath.setText(fileUri.getPath());
            btnModifyDocument.setEnabled(validator.validateFields(fields));
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        return super.onPrepareOptionsMenu(menu);
    }

    private void getComponents() {
        imgBtnFindDocument = (ImageButton) contentView.findViewById(R.id.ib_find_document);
        btnModifyDocument = (Button) contentView.findViewById(R.id.btn_modify_document);
        btnUploadedDocument = (Button) contentView.findViewById(R.id.btn_uploaded_document);

        etDocumentName = (EditText) contentView.findViewById(R.id.et_document_name);
        tvDocumentPath = (TextView) contentView.findViewById(R.id.tv_document_path);

        fields = new TextView[]{etDocumentName, tvDocumentPath};
    }

    private void setComponentAttributes() {
        imgBtnFindDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performFileSearch();
            }
        });
        btnModifyDocument.setOnClickListener(saveDocument);

        btnUploadedDocument.setOnClickListener(viewDocument);

        etDocumentName.addTextChangedListener(new TextValidator(etDocumentName) {
            @Override
            public void validate(TextView textView, String text) {
                if (text != null && !text.equals(""))
                    document.setName(text);
                btnModifyDocument.setEnabled(validator.validateFields(fields));
            }
        });
    }

    public void performFileSearch() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    public void getDocument() {
        Map<String, String> params = new ArrayMap<>();
        params.put("userId", id);
        OkHttpServletRequest request = new OkHttpServletRequest(getBaseContext());
        Request builtRequest = request.buildRequest(Servlets.DOCUMENT, RequestType.GET, params);
        OkHttpClient client = request.buildClient();
        client.newCall(builtRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                processResults(null, true);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                processResults(response.body().string(), true);
            }
        });
    }

    private void processResults(final String response, final boolean get) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (response != null && JsonUtils.isValidJson(response)) {
                    JsonObject json = (JsonObject) new JsonParser().parse(response);
                    if (json.has("data")) {
                        document = new Gson().fromJson(json.get("data").getAsString(), Document.class);
                        if (get) {
                            if (document.getState() != Document.PENDING)
                                showStateDialog(document.getState());
                            btnUploadedDocument.setVisibility(View.VISIBLE);
                        } else
                            Toast.makeText(DocumentsActivity.this, getString(R.string.msj_document), Toast.LENGTH_SHORT).show();
                        String filename = document.getName();
                        etDocumentName.setText(filename.substring(0, filename.lastIndexOf('.')));
                    } else if (json.has("warnings")) {
                        JsonObject warnings = json.getAsJsonObject("warnings");
                        if (warnings.has("noneFound"))
                            Toast.makeText(DocumentsActivity.this, "No ha subido ningún documento", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(DocumentsActivity.this, getString(R.string.error_server), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DocumentsActivity.this, getString(R.string.error_server), Toast.LENGTH_SHORT).show();
                }
                hideProgressDialog();
            }
        });
    }

    private View.OnClickListener viewDocument = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(document.getSource()));
            startActivity(intent);
        }
    };

    private FloatingActionButton.OnClickListener saveDocument = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showProgressDialog();
            if (document != null)
                document.setUserId(id);

            int index = fileUri.toString().lastIndexOf(".");
            String finalName = document.getName() + fileUri.toString().substring(index);
            document.setName(finalName);

            Map<String, String> params = new ArrayMap<>();
            params.put("document", document.toString());

            OkHttpServletRequest request = new OkHttpServletRequest(DocumentsActivity.this);
            String url = request.buildUrl(Servlets.DOCUMENT);
            okhttp3.MultipartBody.Builder multipartBodyBuilder = new okhttp3.MultipartBody.Builder();
            multipartBodyBuilder.setType(okhttp3.MultipartBody.FORM);
            if (params != null) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    multipartBodyBuilder.addFormDataPart(entry.getKey(), entry.getValue());
                }
            }

            MediaType type = MediaType.parse(getContentResolver().getType(fileUri));
            multipartBodyBuilder.addFormDataPart(
                    "file",
                    document.getName(),
                    okhttp3.RequestBody.create(type, fileBytes)
            );
            Request.Builder requestBuilder = new Request.Builder().url(url);
            Request builtRequest = requestBuilder.post(multipartBodyBuilder.build()).build();
            OkHttpClient client = request.buildClient();
            client.newCall(builtRequest).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    processResults(null, false);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    processResults(response.body().string(), false);
                }
            });
        }
    };

    private void showStateDialog(int state) {
        String title = (state == Document.VALID ? getString(R.string.title_dialog_valid_document) : getString(R.string.title_dialog_invalid_document));

        AlertDialog dialog = new AlertDialog.Builder(DocumentsActivity.this)
                .setView(R.layout.dialog_document_state)
                .setTitle(title)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.btn_close), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();

        TextView tvDocumentState = (TextView) dialog.findViewById(R.id.tv_msj_document_state);
        String message = (state == Document.INVALID ? getString(R.string.tv_invalid_document) : getString(R.string.tv_valid_document));
        tvDocumentState.setText(message);
    }
}
