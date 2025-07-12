package com.tallerwebi.helpers;


import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
@RestController
public class HelperMercadoPago {

    private static final String ACCESS_TOKEN = "TU_ACCESS_TOKEN"; // Reemplazá por tu token real

    // 📨 Webhook que recibe notificaciones de pago desde Mercado Pago
    @RequestMapping(value = "/webhook", method = RequestMethod.POST)
    public ResponseEntity<String> recibirWebhook(@RequestBody Map<String, Object> payload,
                                                 @RequestHeader Map<String, String> headers) {

        System.out.println("📨 Notificación recibida de Mercado Pago");
        System.out.println("Encabezados: " + headers);
        System.out.println("Payload: " + payload);

        String tipo = (String) payload.get("type");
        Map<String, Object> datos = (Map<String, Object>) payload.get("data");

        if ("payment".equalsIgnoreCase(tipo) && datos != null) {
            String paymentId = datos.get("id").toString();
            System.out.println("💰 ID del pago recibido: " + paymentId);

            // TODO: Consultar estado del pago en Mercado Pago
            // TODO: Extraer external_reference (id del usuario)
            // TODO: Validar estado aprobado
            // TODO: Otorgar monedas al usuario correspondiente
        }

        return ResponseEntity.ok("Webhook procesado");
    }

    // 🔐 Flujo alternativo (con token de tarjeta) — Checkout API
    public JSONObject crearPagoConTarjeta(double monto, String emailUsuario, long idUsuario,
                                          String tokenTarjeta, String metodoPago) throws Exception {
        URL url = new URL("https://api.mercadopago.com/v1/payments");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Authorization", "Bearer " + ACCESS_TOKEN);
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);

        JSONObject jsonPago = new JSONObject();
        jsonPago.put("transaction_amount", monto);
        jsonPago.put("installments", 1);
        jsonPago.put("payment_method_id", metodoPago);
        jsonPago.put("token", tokenTarjeta);
        jsonPago.put("description", "Compra de monedas");
        jsonPago.put("external_reference", "usuario_" + idUsuario);

        JSONObject payer = new JSONObject();
        payer.put("email", emailUsuario);
        jsonPago.put("payer", payer);

        OutputStream os = con.getOutputStream();
        os.write(jsonPago.toString().getBytes());
        os.flush();
        os.close();

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null)
            response.append(inputLine);
        in.close();

        return new JSONObject(response.toString());
    }

    // 🌐 Endpoint público para que el frontend solicite preferencia de pago (Checkout Pro)
    @RequestMapping(value = "/crear-preferencia", method = RequestMethod.POST)
    public ResponseEntity<Map<String, String>> crearPreferencia(@RequestBody Map<String, String> datos) {
        String userId = datos.get("userId");
        String paquete = datos.get("paquete"); // ej: "100 monedas"
        String email = datos.get("email");

        try {
            String initPoint = generarPreferenciaCheckoutPro(userId, paquete, email);
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("init_point", initPoint);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "No se pudo generar la preferencia"));
        }
    }

    // 🧠 Método interno para crear la preferencia y obtener el init_point
    private String generarPreferenciaCheckoutPro(String externalRef, String titulo, String emailUsuario) throws Exception {
        URL url = new URL("https://api.mercadopago.com/checkout/preferences");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Authorization", "Bearer " + ACCESS_TOKEN);
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);

        JSONObject preferencia = new JSONObject();
        preferencia.put("external_reference", externalRef);

        JSONArray items = new JSONArray();
        JSONObject item = new JSONObject();
        item.put("title", titulo);
        item.put("quantity", 1);
        item.put("unit_price", 50); // Podés ajustar el valor según el paquete
        items.put(item);
        preferencia.put("items", items);

        if (emailUsuario != null && !emailUsuario.isEmpty()) {
            JSONObject payer = new JSONObject();
            payer.put("email", emailUsuario);
            preferencia.put("payer", payer);
        }

        OutputStream os = con.getOutputStream();
        os.write(preferencia.toString().getBytes());
        os.flush();
        os.close();

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null)
            response.append(inputLine);
        in.close();

        JSONObject respuesta = new JSONObject(response.toString());
        return respuesta.getString("init_point");
    }
}





