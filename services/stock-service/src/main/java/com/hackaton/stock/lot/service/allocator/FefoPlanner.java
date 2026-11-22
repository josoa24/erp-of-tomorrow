package com.hackaton.stock.lot.service.allocator;

import com.hackaton.stock.lot.dto.LotBalance;
import com.hackaton.stock.lot.dto.LotTake;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class FefoPlanner {

    private static final Comparator<LotBalance> FIRST_EXPIRED_FIRST = Comparator
            .comparing(LotBalance::expiryDate)
            .thenComparing(LotBalance::receivedAt)
            .thenComparing(LotBalance::lotId);

    public List<LotTake> plan(List<LotBalance> lots, int quantity) {
        List<LotBalance> openLots = lots.stream()
                .filter(lot -> lot.remaining() > 0)
                .sorted(FIRST_EXPIRED_FIRST)
                .toList();
        List<LotTake> takes = new ArrayList<>();
        int left = quantity;
        for (LotBalance lot : openLots) {
            if (left == 0) {
                break;
            }
            int taken = (int) Math.min(left, lot.remaining());
            takes.add(new LotTake(lot.lotId(), taken));
            left -= taken;
        }
        return takes;
    }
}
