package com.micfro.moneycard;

import com.micfro.moneycard.model.MoneyCard;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class MoneyCardJsonTest {

  @Autowired
  private JacksonTester<MoneyCard> json;

  @Autowired
  private JacksonTester<MoneyCard[]> jsonList;

  private MoneyCard[] moneyCards;

  @BeforeEach
  void setUp() {
    moneyCards = Arrays.array(
        new MoneyCard(99L, 123.45, "sarah1"),
        new MoneyCard(100L, 1.00, "sarah1"),
        new MoneyCard(101L, 150.00, "sarah1"));
  }

  @Test
  void moneyCardSerializationTest() throws IOException {
    MoneyCard moneyCard = moneyCards[0];
    assertThat(json.write(moneyCard)).isStrictlyEqualToJson("single.json");
    assertThat(json.write(moneyCard)).hasJsonPathNumberValue("@.id");
    assertThat(json.write(moneyCard)).extractingJsonPathNumberValue("@.id")
        .isEqualTo(99);
    assertThat(json.write(moneyCard)).hasJsonPathNumberValue("@.amount");
    assertThat(json.write(moneyCard)).extractingJsonPathNumberValue("@.amount")
        .isEqualTo(123.45);
  }

  @Test
  void moneyCardDeserializationTest() throws IOException {
    String expected = """
                {
                    "id": 99,
                    "amount": 123.45,
                    "owner": "sarah1"
                }
                """;
    assertThat(json.parse(expected))
        .isEqualTo(new MoneyCard(99L, 123.45, "sarah1"));
    assertThat(json.parseObject(expected).id()).isEqualTo(99);
    assertThat(json.parseObject(expected).amount()).isEqualTo(123.45);
  }



  //  A Serialization test
  @Test
  void moneyCardListSerializationTest() throws IOException {
    assertThat(jsonList.write(moneyCards)).isStrictlyEqualToJson("list.json");
  }


//  A deserialization test
  @Test
  void moneyCardListDeserializationTest() throws IOException {
    String expected="""
         [
            { "id": 99, "amount": 123.45, "owner": "sarah1" },
            { "id": 100, "amount": 1.00, "owner": "sarah1" },
            { "id": 101, "amount": 150.00, "owner": "sarah1" }
         ]
         """;
    assertThat(jsonList.parse(expected)).isEqualTo(moneyCards);
  }


}
