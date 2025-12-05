package springboot.bg.harisauto.cart;

import org.junit.jupiter.api.Test;
import springboot.bg.harisauto.service.model.CarService;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ShoppingCartTest {

    @Test
    void addItem_addsOnlyUniqueById_andCountsTotal() {
        ShoppingCart cart = new ShoppingCart();
        CarService s1 = CarService.builder().id(UUID.randomUUID()).name("Oil").basePrice(new BigDecimal("10.00")).build();
        CarService s1Duplicate = CarService.builder().id(s1.getId()).name("Oil").basePrice(new BigDecimal("10.00")).build();
        CarService s2 = CarService.builder().id(UUID.randomUUID()).name("Tire").basePrice(new BigDecimal("20.00")).build();

        cart.addItem(s1);
        cart.addItem(s1Duplicate); // should be ignored
        cart.addItem(s2);

        assertThat(cart.getCount()).isEqualTo(2);
        assertThat(cart.getTotal()).isEqualTo(new BigDecimal("30.00"));
    }

    @Test
    void removeItem_andClear_behaveAsExpected() {
        ShoppingCart cart = new ShoppingCart();
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        cart.addItem(CarService.builder().id(id1).name("A").basePrice(new BigDecimal("5.00")).build());
        cart.addItem(CarService.builder().id(id2).name("B").basePrice(new BigDecimal("7.00")).build());

        cart.removeItem(id1);
        assertThat(cart.getCount()).isEqualTo(1);
        assertThat(cart.getTotal()).isEqualTo(new BigDecimal("7.00"));

        cart.clear();
        assertThat(cart.getCount()).isZero();
        assertThat(cart.getTotal()).isEqualTo(BigDecimal.ZERO);
    }
}
