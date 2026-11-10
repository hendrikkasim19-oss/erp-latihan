package id.nusantararasa.erp.customer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import id.nusantararasa.erp.faktur.Faktur;
import id.nusantararasa.erp.faktur.FakturRepository;
import id.nusantararasa.erp.faktur.StatusFaktur;
import id.nusantararasa.erp.umum.DataTidakDitemukanException;

@Service
public class PiutangService {

    private final CustomerRepository customerRepository;
    private final FakturRepository fakturRepository;

    public PiutangService(CustomerRepository customerRepository, FakturRepository fakturRepository) {
        this.customerRepository = customerRepository;
        this.fakturRepository = fakturRepository;
    }

    @Transactional(readOnly = true)
    public PiutangDto piutang(String kodeCustomer) {
        Customer customer = customerRepository.findByKode(kodeCustomer)
                .orElseThrow(() -> new DataTidakDitemukanException("Customer " + kodeCustomer + " tidak ditemukan."));

        List<Faktur> belumLunas = fakturRepository.findByCustomerIdAndStatusOrderByJatuhTempoAscNomorAsc(
                customer.getId(), StatusFaktur.BELUM_LUNAS);
        BigDecimal totalPiutang = skala(fakturRepository.totalPiutang(customer.getId(), StatusFaktur.BELUM_LUNAS));
        BigDecimal limitKredit = skala(customer.getLimitKredit());

        List<PiutangDto.FakturBelumLunas> faktur = belumLunas.stream()
                .map(f -> new PiutangDto.FakturBelumLunas(f.getNomor(), f.getTanggal(), f.getJatuhTempo(),
                        skala(f.getTotal())))
                .toList();

        return new PiutangDto(customer.getKode(), customer.getNama(), limitKredit, totalPiutang,
                limitKredit.subtract(totalPiutang), faktur);
    }

    private static BigDecimal skala(BigDecimal nilai) {
        return nilai.setScale(2, RoundingMode.HALF_UP);
    }
}
