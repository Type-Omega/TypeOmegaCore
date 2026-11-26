package com.typeomega.core.kardashev;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;

import com.raishxn.gtna.common.machine.multiblock.energy.GeneratorArrayMachine;
import com.raishxn.gtna.common.machine.multiblock.part.energy.WirelessDynamoHatchPartMachine;
import com.typeomega.core.TypeOmegaMod;

import java.lang.reflect.Field;

/** The GTNA side of the anti-double-count rule; a class of its own so {@link KardashevLocal} loads without GTNA. */
final class KardashevGtna {

    private static final Field WIRELESS_MODE = wirelessModeField();

    private KardashevGtna() {}

    static boolean routedToWireless(MetaMachine machine) {
        if (machine instanceof WirelessDynamoHatchPartMachine) return true;
        if (!(machine instanceof IMultiPart part)) return false;
        for (var controller : part.getControllers()) {
            if (controller instanceof GeneratorArrayMachine array && wireless(array)) return true;
        }
        return false;
    }

    // EN: A Generator Array in wireless mode drains its dynamo hatches into the network, which counts the EU; GTNA
    // keeps the flag private, so it is read by reflection (once resolved, a field read costs about nothing). If the
    // field is gone the array's energy is not counted here: an undercount is safer than a double count.
    // PT: Um Generator Array em modo sem fio esvazia os hatches dínamo na rede, que já conta o EU; o GTNA deixa a
    // flag privada, então ela é lida por reflexão (resolvido o campo, a leitura custa quase nada). Sem o campo, o EU
    // do array não é contado aqui: contar a menos é mais seguro que contar em dobro.
    // ZH: 无线模式的 Generator Array 会把动力仓的电量转入网络，由网络计数；GTNA 将该标志设为私有，因此用反射读取
    // （字段解析后读取几乎无开销）。若字段不存在则这里不计该阵列的电量：少计比重复计更安全。
    private static boolean wireless(GeneratorArrayMachine array) {
        if (WIRELESS_MODE == null) return true;
        try {
            return WIRELESS_MODE.getBoolean(array);
        } catch (IllegalAccessException e) {
            return true;
        }
    }

    private static Field wirelessModeField() {
        try {
            Field field = GeneratorArrayMachine.class.getDeclaredField("wirelessMode");
            field.setAccessible(true);
            return field;
        } catch (ReflectiveOperationException | RuntimeException e) {
            TypeOmegaMod.LOGGER.warn("Kardashev meter: Generator Array wireless mode not readable; its energy is " +
                    "left to the wireless network", e);
            return null;
        }
    }
}
