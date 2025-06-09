import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ExtenTest {

    @BeforeEach
    void setUp() {
        Exten.products.clear();
        Exten.users.clear();
        Exten.coupons.clear();
    }

    @Test
    void testAddOrMergeProduct_Success() {
        Exten.addOrMergeProduct("Pen", "Stationery", "P001", 5.0, 10);
        assertEquals(1, Exten.products.size());
        Product p = Exten.products.get(0);
        assertEquals("Pen", p.getName());
        assertEquals("Stationery", p.getCategory());
        assertEquals("P001", p.getCode());
        assertEquals(5.0, p.getPrice());
        assertEquals(10, p.getStock());
    }

    @Test
    void testAddOrMergeProduct_DuplicateCode_Throws() {
        Exten.addOrMergeProduct("Pen", "Stationery", "P001", 5.0, 10);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            Exten.addOrMergeProduct("Pencil", "Stationery", "P001", 2.0, 5)
        );
        assertTrue(ex.getMessage().contains("Ийм кодтой бараа бүртгэлтэй байна!"));
    }

    @Test
    void testAddOrMergeProduct_EmptyName_Throws() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            Exten.addOrMergeProduct("", "Stationery", "P001", 5.0, 10)
        );
        assertTrue(ex.getMessage().contains("Барааны нэр хоосон байж болохгүй."));
    }

    @Test
    void testAddOrMergeProduct_NegativePrice_Throws() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            Exten.addOrMergeProduct("Pen", "Stationery", "P001", -1.0, 10)
        );
        assertTrue(ex.getMessage().contains("Үнэ сөрөг байж болохгүй."));
    }

    @Test
    void testDeleteProduct_Success() {
        Exten.addOrMergeProduct("Pen", "Stationery", "P001", 5.0, 10);
        int id = Exten.products.get(0).getId();
        Exten.deleteProduct(id);
        assertEquals(0, Exten.products.size());
    }

    @Test
    void testDeleteProduct_NotFound_Throws() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            Exten.deleteProduct(999)
        );
        assertTrue(ex.getMessage().contains("Бараа олдсонгүй."));
    }

    @Test
    void testSignUp_Success() {
        Exten.signUp("user1", "pass", "user");
        assertEquals(1, Exten.users.size());
        assertEquals("user1", Exten.users.get(0).getUsername());
    }

    @Test
    void testSignUp_DuplicateUsername_Throws() {
        Exten.signUp("user1", "pass", "user");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            Exten.signUp("user1", "pass2", "user")
        );
        assertTrue(ex.getMessage().contains("Ийм нэртэй хэрэглэгч байна!"));
    }

    @Test
    void testAddBalance_Success() {
        Exten.signUp("user1", "pass", "user");
        User u = Exten.users.get(0);
        Exten.addBalance(u, 100.0);
        assertEquals(100.0, u.getBalance());
    }

    @Test
    void testAddBalance_Negative_Throws() {
        Exten.signUp("user1", "pass", "user");
        User u = Exten.users.get(0);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            Exten.addBalance(u, -10.0)
        );
        assertTrue(ex.getMessage().contains("Сөрөг мөнгө нэмэх боломжгүй!"));
    }

    @Test
    void testAddCoupon_Success() {
        Exten.addCoupon("SALE10", 10.0);
        assertTrue(Exten.coupons.containsKey("SALE10"));
        assertEquals(10.0, Exten.coupons.get("SALE10"));
    }

    @Test
    void testAddCoupon_Duplicate_Throws() {
        Exten.addCoupon("SALE10", 10.0);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            Exten.addCoupon("SALE10", 15.0)
        );
        assertTrue(ex.getMessage().contains("Код бүртгэлтэй байна!"));
    }

    @Test
    void testMakeOrder_Success() {
        Exten.addOrMergeProduct("Pen", "Stationery", "P001", 5.0, 10);
        Exten.signUp("user1", "pass", "user");
        User u = Exten.users.get(0);
        Exten.addBalance(u, 100.0);

        List<Integer> ids = List.of(Exten.products.get(0).getId());
        List<Integer> qtys = List.of(2);

        Order o = Exten.makeOrder(u, ids, qtys, null);
        assertEquals(OrderStatus.PAID, o.getStatus());
        assertEquals(90.0, u.getBalance());
        assertEquals(8, Exten.products.get(0).getStock());
    }

    @Test
    void testMakeOrder_InsufficientBalance_Throws() {
        Exten.addOrMergeProduct("Pen", "Stationery", "P001", 50.0, 10);
        Exten.signUp("user1", "pass", "user");
        User u = Exten.users.get(0);
        Exten.addBalance(u, 20.0);

        List<Integer> ids = List.of(Exten.products.get(0).getId());
        List<Integer> qtys = List.of(1);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            Exten.makeOrder(u, ids, qtys, null)
        );
        assertTrue(ex.getMessage().contains("Үлдэгдэл хүрэлцэхгүй"));
    }

    @Test
    void testMakeOrder_InvalidCoupon_Throws() {
        Exten.addOrMergeProduct("Pen", "Stationery", "P001", 5.0, 10);
        Exten.signUp("user1", "pass", "user");
        User u = Exten.users.get(0);
        Exten.addBalance(u, 100.0);

        List<Integer> ids = List.of(Exten.products.get(0).getId());
        List<Integer> qtys = List.of(1);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            Exten.makeOrder(u, ids, qtys, "NOTEXIST")
        );
        assertTrue(ex.getMessage().contains("Купон код буруу."));
    }
}