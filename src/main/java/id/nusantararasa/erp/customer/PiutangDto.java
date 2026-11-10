package id.nusantararasa.erp.customer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PiutangDto(
        String kodeCustomer,
        String namaCustomer,
        BigDecimal limitKredit,
        BigDecimal totalPiutang,
        BigDecimal sisaLimit,
        List<FakturBelumLunas> faktur) {

    public record FakturBelumLunas(String nomor, LocalDate tanggal, LocalDate jatuhTempo, BigDecimal total) {
    }
}
