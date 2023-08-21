package io.github.elenaaltuhova.vouchersystem.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@Service
public class VoucherCodesGeneratorService {
    public List<UUID> generateUUIDCodes(int count) {
        List<UUID> codeList = new ArrayList<>();
        IntStream.range(0, count).forEach(i -> {
            codeList.add(UUID.randomUUID());
        });
        return codeList;
    }
}
