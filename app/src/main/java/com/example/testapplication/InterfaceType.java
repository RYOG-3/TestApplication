package com.example.testapplication;

public enum InterfaceType {
    FastEthernet_00(00),
    FastEthernet_01(01),
    FastEthernet_02(02),
    FastEthernet_03(03),
    FastEthernet_04(04),
    FastEthernet_05(05),
    FastEthernet_06(06),
    FastEthernet_07(07),
    GigabitEthernet_00(00),
    GigabitEthernet_01(01),
    GigabitEthernet_02(02),
    GigabitEthernet_03(03),
    GigabitEthernet_04(04),
    GigabitEthernet_05(05),
    GigabitEthernet_06(06),
    GigabitEthernet_07(07),
    GigabitEthernet_000(000),
    GigabitEthernet_001(001),
    GigabitEthernet_101(101),
    GigabitEthernet_102(102),
    GigabitEthernet_103(103),
    GigabitEthernet_104(104),
    GigabitEthernet_105(105),
    GigabitEthernet_106(106),
    GigabitEthernet_107(107),
    GigabitEthernet_108(108),
    GigabitEthernet_109(109),
    GigabitEthernet_1010(1010),
    GigabitEthernet_1011(1011),
    GigabitEthernet_1012(1012);

    private int id;

    private InterfaceType(int id) {
        this.id = id;
    }
}
