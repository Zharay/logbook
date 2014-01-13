/**
 * No Rights Reserved.
 * This program and the accompanying materials
 * are made available under the terms of the Public Domain.
 */
package logbook.config.bean;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 所有艦娘のグループを保存します
 *
 */
public final class ShipGroupBean {

    /** グループ名 */
    private String name;

    /** 艦娘リスト */
    private Set<Long> ships = new LinkedHashSet<Long>();

    /**
     * グループ名を取得します。
     * @return グループ名
     */
    public String getName() {
        return this.name;
    }

    /**
     * グループ名を設定します。
     * @param name グループ名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 艦娘リストを取得します。
     * @return 艦娘リスト
     */
    public Set<Long> getShips() {
        return this.ships;
    }

    /**
     * 艦娘リストを設定します。
     * @param ships 艦娘リスト
     */
    public void setShips(Set<Long> ships) {
        this.ships = ships;
    }
}
