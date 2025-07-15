package com.tallerwebi.helpers;


import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class HelperMercadoPago {

    private static final String ACCESS_TOKEN = "TEST-438418116415624-071115-01937db3e4a01d517251dd5a56e9cdbc-437743476";

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
        }

        return ResponseEntity.ok("Webhook procesado");
    }


    public static JSONObject pagarSandbox(int monto, String emailComprador, String cardToken, long idUsuario) throws Exception {
        URL url = new URL("https://api.mercadopago.com/v1/payments");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Authorization", "Bearer " + ACCESS_TOKEN);
        con.setRequestProperty("Content-Type", "application/json");

        // Generar un valor único para esta transacción
        String idemKey = UUID.randomUUID().toString();
        con.setRequestProperty("X-Idempotency-Key", idemKey);
        con.setDoOutput(true);

        // Construir el JSON del payload
        JSONObject payload = new JSONObject();
        payload.put("transaction_amount", monto);
        payload.put("token", cardToken);
        payload.put("description", "Compra de monedas");
        payload.put("installments", 1);
        payload.put("payment_method_id", "visa");  //
        payload.put("payer", new JSONObject().put("email", emailComprador));
        JSONObject metadata = new JSONObject();
        metadata.put("userId", idUsuario);
        metadata.put("monedas", monto);
        payload.put("metadata", metadata);

        try (OutputStream os = con.getOutputStream()) {
            os.write(payload.toString().getBytes());
        }

        int status = con.getResponseCode();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                status == 201 ? con.getInputStream() : con.getErrorStream()
        ));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) sb.append(line);
        reader.close();

        JSONObject response = new JSONObject(sb.toString());
        String detalle = response.optString("status_detail", "unknown");
        String traduccion = traducirStatusDetail(detalle);
        response.put("estadoTraducido", traduccion);

        if (status != 201) {

            throw new RuntimeException("Pago fallido: " + traduccion);
        }

        return response;
    }


    private static String traducirStatusDetail(String detalle) {
        switch (detalle) {
            case "accredited":
                return "¡Pago aprobado con éxito!";
            case "cc_rejected_bad_filled_card_number":
                return "Número de tarjeta inválido. Verifícalo e intenta nuevamente.";
            case "cc_rejected_bad_filled_date":
                return "Fecha de vencimiento incorrecta.";
            case "cc_rejected_bad_filled_other":
                return "Datos incompletos o inválidos.";
            case "cc_rejected_bad_filled_security_code":
                return "Código de seguridad inválido.";
            case "cc_rejected_blacklist":
                return "Tarjeta no autorizada. Usá otro medio.";
            case "cc_rejected_call_for_authorize":
                return "Debés autorizar este pago con tu banco.";
            case "cc_rejected_card_disabled":
                return "Tarjeta inactiva. Contactá a tu banco.";
            case "cc_rejected_card_error":
                return "No se pudo procesar la tarjeta.";
            case "cc_rejected_duplicated_payment":
                return "Este pago ya fue realizado.";
            case "cc_rejected_high_risk":
                return "Pago rechazado por seguridad. Usá otro método.";
            case "cc_rejected_insufficient_amount":
                return "Fondos insuficientes.";
            case "cc_rejected_invalid_installments":
                return "Cuotas no disponibles para tu tarjeta.";
            case "cc_rejected_max_attempts":
                return "Superaste el número de intentos. Probá más tarde.";
            case "cc_rejected_other_reason":
                return "Tu pago fue rechazado. Probá con otra tarjeta.";
            default:
                return "Hubo un error procesando el pago. Intentalo de nuevo.";
        }




}
}






