package fxtg;

import java.io.Serializable;

/**
 * fxtgPriceAndAmount用来表示一个动作（不区分买家和卖家），包含价格和数量以及该动作的ID。
 *
 * @author xiaodai
 */
public class FxtgPriceAndAmount implements Serializable {

    /**
     * 价格。
     */
    public double price;
    /**
     * 数量。
     */
    public int amount;
    /**
     * 此动作的ID（BuyID或SellID）。
     */
    public Long ID;
}
