package cn.allbs.weightscale.enums;

/**
 * 接口 BasicEnum
 *
 * @author ChenQi
 * @date 2024/2/29
 */

import com.fasterxml.jackson.annotation.JsonValue;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

/**
 * 通用枚举接口
 *
 * @param <V> 枚举值的类型
 * @param <E> 子枚举类型
 * @author vains
 */
public interface BasicEnum<V extends Serializable, E extends Enum<E>> {

    @JsonValue
    V getValue();

    /**
     * 根据子枚举和子枚举对应的入参值找到对应的枚举类型
     *
     * @param value 子枚举中对应的值
     * @param clazz 子枚举类型
     * @param <B>   {@link BasicEnum} 的子类类型
     * @param <V>   子枚举值的类型
     * @param <E>   子枚举的类型
     * @return 返回 {@link BasicEnum} 对应的子类实例
     */
    static <B extends BasicEnum<V, E>, V extends Serializable, E extends Enum<E>> B fromValue(V value, Class<B> clazz) {
        return Arrays.stream(clazz.getEnumConstants())
                .filter(e -> Objects.equals(e.getValue(), value))
                .findFirst().orElse(null);
    }

}
