 new Thread(new Runnable() {
    public void run() {
        ApacheServletRequest servletRequest = new ApacheServletRequest(getApplicationContext(), true);

        String inputString = email.getText().toString();
        JSONObject user = new JSONObject();
        try {
            user.put("email", inputString);
            user.put("password", inputString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Map<String, JSONObject> params = new HashMap<String, JSONObject>();
        params.put("user", user);

        URI uri = null;
        try {
            uri = servletRequest.create(Servlets.LOGIN, params);
            JSONObject jsonResponse = servletRequest.execute(RequestType.GET, uri);
            //Toast.makeText(getApplicationContext(), jsonResponse.toString(), Toast.LENGTH_LONG);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}).start();