package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ServicioImplementacion.UsuarioServiceImpl;
import com.tallerwebi.dominio.interfaceService.UsuarioService;
import com.tallerwebi.helpers.HelperMercadoPago;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;

@Controller
public class MarketController {

    private final UsuarioService usuarioService;

    public MarketController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/market")
    public String irAlMarket() {
        return "market";
    }


    @PostMapping("/pagar")
        public ModelAndView procesarPago(
            @RequestParam int monto,
            @RequestParam String cardToken,
            HttpSession session
    ) {

        ModelMap model = new ModelMap();

        // 1) Obtener id de la sesión
        Long idUsuario = (Long) session.getAttribute("idUsuario");


        if (idUsuario == null) {
            model.addAttribute("error", "Debe iniciar sesión antes de pagar");
            return new ModelAndView("login", model);
        }
        // Supongamos que obtienes el email así:
        String email = usuarioService.obtenerEmailPorId(idUsuario);
        System.out.println("[/pagar] monto=" + monto + ", token=" + cardToken + ", email=" + email);

        // 2) Llamar al helper
        JSONObject respuesta;
        try {
            respuesta = HelperMercadoPago.pagarSandbox(monto, email, cardToken, idUsuario);
            System.out.println("🔎 Respuesta MP: " + respuesta.toMap());
            if ("approved".equals(respuesta.optString("status"))) usuarioService.agregarMonedasAlUsuario(respuesta);

        } catch (Exception e) {
            model.addAttribute("error", "Error al procesar pago: " + e.getMessage());
            model.addAttribute("pagoResponse", null); // <-- asegurás que exista
            return new ModelAndView("pago-result", model);
        }

        // 3) Pasar la respuesta al view

        model.addAttribute("pagoResponse", respuesta.toMap());
        model.addAttribute("monedas", usuarioService.getMonedasPorIdUsuario(idUsuario));
        return new ModelAndView("pago-result", model);
    }
    @GetMapping("/pago")
    public ModelAndView mostrarPago(@RequestParam int amount, HttpSession session) {
        ModelMap model = new ModelMap();
         model.addAttribute("monto", amount);
        return new ModelAndView("formulario-de-pago", model);
    }

}



