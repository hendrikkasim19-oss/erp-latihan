package id.nusantararasa.erp.customer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class PiutangApiTest {

    @Autowired
    MockMvc mvc;

    @Test
    void piutangCustomerDenganFakturBelumLunas() throws Exception {
        mvc.perform(get("/api/customers/CUS-021/piutang"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.kodeCustomer").value("CUS-021"))
                .andExpect(jsonPath("$.namaCustomer").value("Toko Berkah Abadi"))
                .andExpect(jsonPath("$.limitKredit").value(40000000.0))
                .andExpect(jsonPath("$.totalPiutang").value(36750000.0))
                .andExpect(jsonPath("$.sisaLimit").value(3250000.0));
    }

    @Test
    void daftarFakturBelumLunasUrutJatuhTempo() throws Exception {
        mvc.perform(get("/api/customers/CUS-021/piutang"))
                .andExpect(jsonPath("$.faktur.length()").value(1))
                .andExpect(jsonPath("$.faktur[0].nomor").value("INV/2026/10/00006"))
                .andExpect(jsonPath("$.faktur[0].tanggal").value("2026-10-08"))
                .andExpect(jsonPath("$.faktur[0].jatuhTempo").value("2026-10-22"))
                .andExpect(jsonPath("$.faktur[0].total").value(36750000.0));
    }

    @Test
    void fakturLunasTidakDihitung() throws Exception {
        mvc.perform(get("/api/customers/CUS-001/piutang"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPiutang").value(0.0))
                .andExpect(jsonPath("$.sisaLimit").value(1200000000.0))
                .andExpect(jsonPath("$.faktur").isEmpty())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("\"totalPiutang\":0.00")));
    }

    @Test
    void customerTidakAda() throws Exception {
        mvc.perform(get("/api/customers/CUS-999/piutang"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.pesan").value("Customer CUS-999 tidak ditemukan."));
    }
}
