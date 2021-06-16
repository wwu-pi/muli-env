package de.wwu.muli.tcg.concrete_programs;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/*
 * Copyright (C) 2014, United States Government, as represented by the
 * Administrator of the National Aeronautics and Space Administration.
 * All rights reserved.
 *
 * Symbolic Pathfinder (jpf-symbc) is licensed under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* 2021-06-14 Taken and adjusted from https://github.com/sosy-lab/sv-benchmarks/blob/master/java/java-ranger-regression/WBS/impl/WBS.java
    : HW */

public class WBS {

    //Internal state
    private int WBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE;
    private int WBS_Node_WBS_BSCU_rlt_PRE1;
    private int WBS_Node_WBS_rlt_PRE2;

    //Outputs
    private int Nor_Pressure;
    private int Alt_Pressure;
    private int Sys_Mode;

    public WBS() {
        WBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE = 0;
        WBS_Node_WBS_BSCU_rlt_PRE1 = 0;
        WBS_Node_WBS_rlt_PRE2 = 100;
        Nor_Pressure = 0;
        Alt_Pressure = 0;
        Sys_Mode = 0;
    }

    /* 2021-06-14 Now returns WBS : HW */
    public static WBS launch(int pedal1, boolean auto1, boolean skid1, int pedal2, boolean auto2, boolean skid2, int pedal3, boolean auto3, boolean skid3) {
        WBS wbs = new WBS();
        wbs.update(pedal1, auto1, skid1);
        wbs.update(pedal2, auto2, skid2);
        wbs.update(pedal3, auto3, skid3);
        return wbs;
    }

    /* 2021-06-14 Now returns WBS : HW */
    public WBS update(int PedalPos, boolean AutoBrake,
                       boolean Skid) {
        int WBS_Node_WBS_AS_MeterValve_Switch;
        int WBS_Node_WBS_AccumulatorValve_Switch;
        int WBS_Node_WBS_BSCU_Command_AntiSkidCommand_Normal_Switch;
        boolean WBS_Node_WBS_BSCU_Command_Is_Normal_Relational_Operator;
        int WBS_Node_WBS_BSCU_Command_PedalCommand_Switch1;
        int WBS_Node_WBS_BSCU_Command_Switch;
        boolean WBS_Node_WBS_BSCU_SystemModeSelCmd_Logical_Operator6;
        int WBS_Node_WBS_BSCU_SystemModeSelCmd_Unit_Delay;
        int WBS_Node_WBS_BSCU_Switch2;
        int WBS_Node_WBS_BSCU_Switch3;
        int WBS_Node_WBS_BSCU_Unit_Delay1;
        int WBS_Node_WBS_Green_Pump_IsolationValve_Switch;
        int WBS_Node_WBS_SelectorValve_Switch;
        int WBS_Node_WBS_SelectorValve_Switch1;
        int WBS_Node_WBS_Unit_Delay2;

        WBS_Node_WBS_Unit_Delay2 = WBS_Node_WBS_rlt_PRE2;
        WBS_Node_WBS_BSCU_Unit_Delay1 = WBS_Node_WBS_BSCU_rlt_PRE1;
        WBS_Node_WBS_BSCU_SystemModeSelCmd_Unit_Delay = WBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE;

        WBS_Node_WBS_BSCU_Command_Is_Normal_Relational_Operator = (WBS_Node_WBS_BSCU_SystemModeSelCmd_Unit_Delay == 0);

        if ((PedalPos == 0)) {
            WBS_Node_WBS_BSCU_Command_PedalCommand_Switch1 = 0;
        } else {
            if ((PedalPos == 1)) {
                WBS_Node_WBS_BSCU_Command_PedalCommand_Switch1 = 1;
            }  else {
                if ((PedalPos == 2)) {
                    WBS_Node_WBS_BSCU_Command_PedalCommand_Switch1 = 2;
                } else {
                    if ((PedalPos == 3)) {
                        WBS_Node_WBS_BSCU_Command_PedalCommand_Switch1 = 3;
                    } else {
                        if ((PedalPos == 4)) {
                            WBS_Node_WBS_BSCU_Command_PedalCommand_Switch1 = 4;
                        }  else {
                            WBS_Node_WBS_BSCU_Command_PedalCommand_Switch1 = 0;
                        }
                    }
                }
            }
        }

        if ((AutoBrake &&
                WBS_Node_WBS_BSCU_Command_Is_Normal_Relational_Operator)) {
            WBS_Node_WBS_BSCU_Command_Switch = 1;
        }  else {
            WBS_Node_WBS_BSCU_Command_Switch = 0;
        }

        WBS_Node_WBS_BSCU_SystemModeSelCmd_Logical_Operator6 = ((((!(WBS_Node_WBS_BSCU_Unit_Delay1 == 0)) &&
                (WBS_Node_WBS_Unit_Delay2 <= 0)) &&
                WBS_Node_WBS_BSCU_Command_Is_Normal_Relational_Operator) ||
                (!WBS_Node_WBS_BSCU_Command_Is_Normal_Relational_Operator));

        if (WBS_Node_WBS_BSCU_SystemModeSelCmd_Logical_Operator6) {
            if (Skid)
                WBS_Node_WBS_BSCU_Switch3 = 0;
            else
                WBS_Node_WBS_BSCU_Switch3 = 4;
        }
        else {
            WBS_Node_WBS_BSCU_Switch3 = 4;
        }

        if (WBS_Node_WBS_BSCU_SystemModeSelCmd_Logical_Operator6) {
            WBS_Node_WBS_Green_Pump_IsolationValve_Switch = 0;
        }  else {
            WBS_Node_WBS_Green_Pump_IsolationValve_Switch = 5;
        }

        if ((WBS_Node_WBS_Green_Pump_IsolationValve_Switch >= 1)) {
            WBS_Node_WBS_SelectorValve_Switch1 = 0;
        }
        else {
            WBS_Node_WBS_SelectorValve_Switch1 = 5;
        }

        if ((!WBS_Node_WBS_BSCU_SystemModeSelCmd_Logical_Operator6)) {
            WBS_Node_WBS_AccumulatorValve_Switch = 0;
        }  else {
            if ((WBS_Node_WBS_SelectorValve_Switch1 >= 1)) {
                WBS_Node_WBS_AccumulatorValve_Switch = WBS_Node_WBS_SelectorValve_Switch1;
            }
            else {
                WBS_Node_WBS_AccumulatorValve_Switch = 5;
            }
        }

        if ((WBS_Node_WBS_BSCU_Switch3 == 0)) {
            WBS_Node_WBS_AS_MeterValve_Switch = 0;
        }  else {
            if ((WBS_Node_WBS_BSCU_Switch3 == 1))  {
                WBS_Node_WBS_AS_MeterValve_Switch = (WBS_Node_WBS_AccumulatorValve_Switch / 4);
            }  else {
                if ((WBS_Node_WBS_BSCU_Switch3 == 2))  {
                    WBS_Node_WBS_AS_MeterValve_Switch = (WBS_Node_WBS_AccumulatorValve_Switch / 2);
                }  else {
                    if ((WBS_Node_WBS_BSCU_Switch3 == 3)) {
                        WBS_Node_WBS_AS_MeterValve_Switch = ((WBS_Node_WBS_AccumulatorValve_Switch / 4) * 3);
                    }  else {
                        if ((WBS_Node_WBS_BSCU_Switch3 == 4)) {
                            WBS_Node_WBS_AS_MeterValve_Switch = WBS_Node_WBS_AccumulatorValve_Switch;
                        }  else {
                            WBS_Node_WBS_AS_MeterValve_Switch = 0;
                        }
                    }
                }
            }
        }

        if (Skid) {
            WBS_Node_WBS_BSCU_Command_AntiSkidCommand_Normal_Switch = 0;
        }  else {
            WBS_Node_WBS_BSCU_Command_AntiSkidCommand_Normal_Switch = (WBS_Node_WBS_BSCU_Command_Switch+WBS_Node_WBS_BSCU_Command_PedalCommand_Switch1);
        }

        if (WBS_Node_WBS_BSCU_SystemModeSelCmd_Logical_Operator6) {
            Sys_Mode = 1;
        }  else {
            Sys_Mode = 0;
        }

        if (WBS_Node_WBS_BSCU_SystemModeSelCmd_Logical_Operator6) {
            WBS_Node_WBS_BSCU_Switch2 = 0;
        }  else {
            if (((WBS_Node_WBS_BSCU_Command_AntiSkidCommand_Normal_Switch >= 0) &&
                    (WBS_Node_WBS_BSCU_Command_AntiSkidCommand_Normal_Switch < 1))) {
                WBS_Node_WBS_BSCU_Switch2 = 0;
            } else {
                if (((WBS_Node_WBS_BSCU_Command_AntiSkidCommand_Normal_Switch >= 1) &&
                        (WBS_Node_WBS_BSCU_Command_AntiSkidCommand_Normal_Switch < 2)))  {
                    WBS_Node_WBS_BSCU_Switch2 = 1;
                }  else {
                    if (((WBS_Node_WBS_BSCU_Command_AntiSkidCommand_Normal_Switch >= 2) &&
                            (WBS_Node_WBS_BSCU_Command_AntiSkidCommand_Normal_Switch < 3))) {
                        WBS_Node_WBS_BSCU_Switch2 = 2;
                    } else {
                        if (((WBS_Node_WBS_BSCU_Command_AntiSkidCommand_Normal_Switch >= 3) &&
                                (WBS_Node_WBS_BSCU_Command_AntiSkidCommand_Normal_Switch < 4)))  {
                            WBS_Node_WBS_BSCU_Switch2 = 3;
                        } else {
                            WBS_Node_WBS_BSCU_Switch2 = 4;
                        }
                    }
                }
            }
        }

        if ((WBS_Node_WBS_Green_Pump_IsolationValve_Switch >= 1))  {
            WBS_Node_WBS_SelectorValve_Switch = WBS_Node_WBS_Green_Pump_IsolationValve_Switch;
        }  else {
            WBS_Node_WBS_SelectorValve_Switch = 0;
        }

        if ((WBS_Node_WBS_BSCU_Switch2 == 0)) {
            Nor_Pressure = 0;
        }  else {
            if ((WBS_Node_WBS_BSCU_Switch2 == 1)) {
                Nor_Pressure = (WBS_Node_WBS_SelectorValve_Switch / 4);
            }  else {
                if ((WBS_Node_WBS_BSCU_Switch2 == 2)) {
                    Nor_Pressure = (WBS_Node_WBS_SelectorValve_Switch / 2);
                }  else {
                    if ((WBS_Node_WBS_BSCU_Switch2 == 3)) {
                        Nor_Pressure = ((WBS_Node_WBS_SelectorValve_Switch / 4) * 3);
                    } else {
                        if ((WBS_Node_WBS_BSCU_Switch2 == 4)) {
                            Nor_Pressure = WBS_Node_WBS_SelectorValve_Switch;
                        } else {
                            Nor_Pressure = 0;
                        }
                    }
                }
            }
        }

        if ((WBS_Node_WBS_BSCU_Command_PedalCommand_Switch1 == 0)) {
            Alt_Pressure = 0;
        }  else {
            if ((WBS_Node_WBS_BSCU_Command_PedalCommand_Switch1 == 1)) {
                Alt_Pressure = (WBS_Node_WBS_AS_MeterValve_Switch / 4);
            }  else {
                if ((WBS_Node_WBS_BSCU_Command_PedalCommand_Switch1 == 2)) {
                    Alt_Pressure = (WBS_Node_WBS_AS_MeterValve_Switch / 2);
                } else {
                    if ((WBS_Node_WBS_BSCU_Command_PedalCommand_Switch1 == 3)) {
                        Alt_Pressure = ((WBS_Node_WBS_AS_MeterValve_Switch / 4) * 3);
                    } else {
                        if ((WBS_Node_WBS_BSCU_Command_PedalCommand_Switch1 == 4)) {
                            Alt_Pressure = WBS_Node_WBS_AS_MeterValve_Switch;
                        } else {
                            Alt_Pressure = 0;
                        }
                    }
                }
            }
        }

        WBS_Node_WBS_rlt_PRE2 = Nor_Pressure;

        WBS_Node_WBS_BSCU_rlt_PRE1 = WBS_Node_WBS_BSCU_Switch2;

        WBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE = Sys_Mode;
        return this;
    }

    public int getWBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE() {
        return WBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE;
    }

    public void setWBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE(int WBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE) {
        this.WBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE = WBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE;
    }

    public int getWBS_Node_WBS_BSCU_rlt_PRE1() {
        return WBS_Node_WBS_BSCU_rlt_PRE1;
    }

    public void setWBS_Node_WBS_BSCU_rlt_PRE1(int WBS_Node_WBS_BSCU_rlt_PRE1) {
        this.WBS_Node_WBS_BSCU_rlt_PRE1 = WBS_Node_WBS_BSCU_rlt_PRE1;
    }

    public int getWBS_Node_WBS_rlt_PRE2() {
        return WBS_Node_WBS_rlt_PRE2;
    }

    public void setWBS_Node_WBS_rlt_PRE2(int WBS_Node_WBS_rlt_PRE2) {
        this.WBS_Node_WBS_rlt_PRE2 = WBS_Node_WBS_rlt_PRE2;
    }

    public int getNor_Pressure() {
        return Nor_Pressure;
    }

    public void setNor_Pressure(int nor_Pressure) {
        Nor_Pressure = nor_Pressure;
    }

    public int getAlt_Pressure() {
        return Alt_Pressure;
    }

    public void setAlt_Pressure(int alt_Pressure) {
        Alt_Pressure = alt_Pressure;
    }

    public int getSys_Mode() {
        return Sys_Mode;
    }

    public void setSys_Mode(int sys_Mode) {
        Sys_Mode = sys_Mode;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof WBS)) {
            return false;
        } else {
            WBS wbs = (WBS) o;
            return wbs.Alt_Pressure == Alt_Pressure && wbs.Nor_Pressure == Nor_Pressure
                    && wbs.Sys_Mode == Sys_Mode && wbs.WBS_Node_WBS_BSCU_rlt_PRE1 == WBS_Node_WBS_BSCU_rlt_PRE1
                    && wbs.WBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE == WBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE
                    && wbs.WBS_Node_WBS_rlt_PRE2 == WBS_Node_WBS_rlt_PRE2;
        }

    }

    @Test
    public void test_update_0() {
        WBS wBS0 = new WBS();
        Integer integer0 = 0;
        wBS0.setWBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE(integer0);
        wBS0.setWBS_Node_WBS_BSCU_rlt_PRE1(integer0);
        Integer integer1 = 100;
        wBS0.setWBS_Node_WBS_rlt_PRE2(integer1);
        wBS0.setNor_Pressure(integer0);
        wBS0.setAlt_Pressure(integer0);
        wBS0.setSys_Mode(integer0);
        Integer integer2 = 5;
        Boolean boolean0 = false;
        WBS wBS1 = new WBS();
        wBS1.setWBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE(integer0);
        wBS1.setWBS_Node_WBS_BSCU_rlt_PRE1(integer0);
        wBS1.setWBS_Node_WBS_rlt_PRE2(integer0);
        wBS1.setNor_Pressure(integer0);
        wBS1.setAlt_Pressure(integer0);
        wBS1.setSys_Mode(integer0);
        assertEquals(wBS1, wBS0.update(integer2,boolean0,boolean0));
    }

    @Test
    public void test_update_1() {
        WBS wBS0 = new WBS();
        Integer integer0 = 0;
        wBS0.setWBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE(integer0);
        wBS0.setWBS_Node_WBS_BSCU_rlt_PRE1(integer0);
        Integer integer1 = 100;
        wBS0.setWBS_Node_WBS_rlt_PRE2(integer1);
        wBS0.setNor_Pressure(integer0);
        wBS0.setAlt_Pressure(integer0);
        wBS0.setSys_Mode(integer0);
        Integer integer2 = 4;
        Boolean boolean0 = false;
        WBS wBS1 = new WBS();
        wBS1.setWBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE(integer0);
        wBS1.setWBS_Node_WBS_BSCU_rlt_PRE1(integer2);
        Integer integer3 = 5;
        wBS1.setWBS_Node_WBS_rlt_PRE2(integer3);
        wBS1.setNor_Pressure(integer3);
        wBS1.setAlt_Pressure(integer0);
        wBS1.setSys_Mode(integer0);
        assertEquals(wBS1, wBS0.update(integer2,boolean0,boolean0));
    }

    @Test
    public void test_update_2() {
        WBS wBS0 = new WBS();
        Integer integer0 = 0;
        wBS0.setWBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE(integer0);
        wBS0.setWBS_Node_WBS_BSCU_rlt_PRE1(integer0);
        Integer integer1 = 100;
        wBS0.setWBS_Node_WBS_rlt_PRE2(integer1);
        wBS0.setNor_Pressure(integer0);
        wBS0.setAlt_Pressure(integer0);
        wBS0.setSys_Mode(integer0);
        Integer integer2 = 3;
        Boolean boolean0 = false;
        WBS wBS1 = new WBS();
        wBS1.setWBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE(integer0);
        wBS1.setWBS_Node_WBS_BSCU_rlt_PRE1(integer2);
        wBS1.setWBS_Node_WBS_rlt_PRE2(integer2);
        wBS1.setNor_Pressure(integer2);
        wBS1.setAlt_Pressure(integer0);
        wBS1.setSys_Mode(integer0);
        assertEquals(wBS1, wBS0.update(integer2,boolean0,boolean0));
    }

    @Test
    public void test_update_3() {
        WBS wBS0 = new WBS();
        Integer integer0 = 0;
        wBS0.setWBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE(integer0);
        wBS0.setWBS_Node_WBS_BSCU_rlt_PRE1(integer0);
        Integer integer1 = 100;
        wBS0.setWBS_Node_WBS_rlt_PRE2(integer1);
        wBS0.setNor_Pressure(integer0);
        wBS0.setAlt_Pressure(integer0);
        wBS0.setSys_Mode(integer0);
        Integer integer2 = 3;
        Boolean boolean0 = false;
        Boolean boolean1 = true;
        WBS wBS1 = new WBS();
        wBS1.setWBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE(integer0);
        wBS1.setWBS_Node_WBS_BSCU_rlt_PRE1(integer0);
        wBS1.setWBS_Node_WBS_rlt_PRE2(integer0);
        wBS1.setNor_Pressure(integer0);
        wBS1.setAlt_Pressure(integer0);
        wBS1.setSys_Mode(integer0);
        assertEquals(wBS1, wBS0.update(integer2,boolean0,boolean1));
    }

    @Test
    public void test_update_4() {
        WBS wBS0 = new WBS();
        Integer integer0 = 0;
        wBS0.setWBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE(integer0);
        wBS0.setWBS_Node_WBS_BSCU_rlt_PRE1(integer0);
        Integer integer1 = 100;
        wBS0.setWBS_Node_WBS_rlt_PRE2(integer1);
        wBS0.setNor_Pressure(integer0);
        wBS0.setAlt_Pressure(integer0);
        wBS0.setSys_Mode(integer0);
        Integer integer2 = 2;
        Boolean boolean0 = true;
        Boolean boolean1 = false;
        WBS wBS1 = new WBS();
        wBS1.setWBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE(integer0);
        Integer integer3 = 3;
        wBS1.setWBS_Node_WBS_BSCU_rlt_PRE1(integer3);
        wBS1.setWBS_Node_WBS_rlt_PRE2(integer3);
        wBS1.setNor_Pressure(integer3);
        wBS1.setAlt_Pressure(integer0);
        wBS1.setSys_Mode(integer0);
        assertEquals(wBS1, wBS0.update(integer2,boolean0,boolean1));
    }

    @Test
    public void test_update_5() {
        WBS wBS0 = new WBS();
        Integer integer0 = 0;
        wBS0.setWBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE(integer0);
        wBS0.setWBS_Node_WBS_BSCU_rlt_PRE1(integer0);
        Integer integer1 = 100;
        wBS0.setWBS_Node_WBS_rlt_PRE2(integer1);
        wBS0.setNor_Pressure(integer0);
        wBS0.setAlt_Pressure(integer0);
        wBS0.setSys_Mode(integer0);
        Integer integer2 = 1;
        Boolean boolean0 = true;
        Boolean boolean1 = false;
        WBS wBS1 = new WBS();
        wBS1.setWBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE(integer0);
        Integer integer3 = 2;
        wBS1.setWBS_Node_WBS_BSCU_rlt_PRE1(integer3);
        wBS1.setWBS_Node_WBS_rlt_PRE2(integer3);
        wBS1.setNor_Pressure(integer3);
        wBS1.setAlt_Pressure(integer0);
        wBS1.setSys_Mode(integer0);
        assertEquals(wBS1, wBS0.update(integer2,boolean0,boolean1));
    }

    @Test
    public void test_update_6() {
        WBS wBS0 = new WBS();
        Integer integer0 = 0;
        wBS0.setWBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE(integer0);
        wBS0.setWBS_Node_WBS_BSCU_rlt_PRE1(integer0);
        Integer integer1 = 100;
        wBS0.setWBS_Node_WBS_rlt_PRE2(integer1);
        wBS0.setNor_Pressure(integer0);
        wBS0.setAlt_Pressure(integer0);
        wBS0.setSys_Mode(integer0);
        Boolean boolean0 = true;
        Boolean boolean1 = false;
        WBS wBS1 = new WBS();
        wBS1.setWBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE(integer0);
        Integer integer2 = 1;
        wBS1.setWBS_Node_WBS_BSCU_rlt_PRE1(integer2);
        wBS1.setWBS_Node_WBS_rlt_PRE2(integer2);
        wBS1.setNor_Pressure(integer2);
        wBS1.setAlt_Pressure(integer0);
        wBS1.setSys_Mode(integer0);
        assertEquals(wBS1, wBS0.update(integer0,boolean0,boolean1));
    }

    @Test
    public void test_update_7() {
        WBS wBS0 = new WBS();
        Integer integer0 = 0;
        wBS0.setWBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE(integer0);
        wBS0.setWBS_Node_WBS_BSCU_rlt_PRE1(integer0);
        Integer integer1 = 100;
        wBS0.setWBS_Node_WBS_rlt_PRE2(integer1);
        wBS0.setNor_Pressure(integer0);
        wBS0.setAlt_Pressure(integer0);
        wBS0.setSys_Mode(integer0);
        Boolean boolean0 = true;
        WBS wBS1 = new WBS();
        wBS1.setWBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE(integer0);
        wBS1.setWBS_Node_WBS_BSCU_rlt_PRE1(integer0);
        wBS1.setWBS_Node_WBS_rlt_PRE2(integer0);
        wBS1.setNor_Pressure(integer0);
        wBS1.setAlt_Pressure(integer0);
        wBS1.setSys_Mode(integer0);
        assertEquals(wBS1, wBS0.update(integer0,boolean0,boolean0));
    }

    @Test
    public void test_launch_0() {
        Integer integer0 = 5;
        Boolean boolean0 = false;
        Boolean boolean1 = true;
        Integer integer1 = 3;
        Integer integer2 = 0;
        WBS wBS0 = new WBS();
        wBS0.setWBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE(integer2);
        wBS0.setWBS_Node_WBS_BSCU_rlt_PRE1(integer2);
        wBS0.setWBS_Node_WBS_rlt_PRE2(integer2);
        wBS0.setNor_Pressure(integer2);
        wBS0.setAlt_Pressure(integer2);
        wBS0.setSys_Mode(integer2);
        assertEquals(wBS0, WBS.launch(integer0,boolean0,boolean1,integer1,boolean0,boolean1,integer2,boolean1,boolean1));
    }

    @Test
    public void test_launch_1() {
        Integer integer0 = 5;
        Boolean boolean0 = true;
        Boolean boolean1 = false;
        Integer integer1 = 0;
        Integer integer2 = 4;
        WBS wBS0 = new WBS();
        wBS0.setWBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE(integer1);
        wBS0.setWBS_Node_WBS_BSCU_rlt_PRE1(integer2);
        wBS0.setWBS_Node_WBS_rlt_PRE2(integer0);
        wBS0.setNor_Pressure(integer0);
        wBS0.setAlt_Pressure(integer1);
        wBS0.setSys_Mode(integer1);
        assertEquals(wBS0, WBS.launch(integer0,boolean0,boolean1,integer1,boolean0,boolean0,integer2,boolean1,boolean1));
    }

    @Test
    public void test_launch_2() {
        Integer integer0 = 4;
        Boolean boolean0 = true;
        Boolean boolean1 = false;
        Integer integer1 = 5;
        Integer integer2 = 2;
        WBS wBS0 = new WBS();
        Integer integer3 = 0;
        wBS0.setWBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE(integer3);
        wBS0.setWBS_Node_WBS_BSCU_rlt_PRE1(integer2);
        wBS0.setWBS_Node_WBS_rlt_PRE2(integer2);
        wBS0.setNor_Pressure(integer2);
        wBS0.setAlt_Pressure(integer3);
        wBS0.setSys_Mode(integer3);
        assertEquals(wBS0, WBS.launch(integer0,boolean0,boolean1,integer1,boolean1,boolean0,integer2,boolean1,boolean1));
    }

    @Test
    public void test_launch_3() {
        Integer integer0 = 4;
        Boolean boolean0 = true;
        Integer integer1 = 2;
        Boolean boolean1 = false;
        Integer integer2 = 0;
        WBS wBS0 = new WBS();
        wBS0.setWBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE(integer2);
        wBS0.setWBS_Node_WBS_BSCU_rlt_PRE1(integer2);
        wBS0.setWBS_Node_WBS_rlt_PRE2(integer2);
        wBS0.setNor_Pressure(integer2);
        wBS0.setAlt_Pressure(integer2);
        wBS0.setSys_Mode(integer2);
        assertEquals(wBS0, WBS.launch(integer0,boolean0,boolean0,integer1,boolean0,boolean1,integer2,boolean1,boolean0));
    }

    @Test
    public void test_launch_4() {
        Integer integer0 = 3;
        Boolean boolean0 = true;
        Boolean boolean1 = false;
        Integer integer1 = 1;
        Integer integer2 = 2;
        WBS wBS0 = new WBS();
        Integer integer3 = 0;
        wBS0.setWBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE(integer3);
        wBS0.setWBS_Node_WBS_BSCU_rlt_PRE1(integer3);
        wBS0.setWBS_Node_WBS_rlt_PRE2(integer3);
        wBS0.setNor_Pressure(integer3);
        wBS0.setAlt_Pressure(integer3);
        wBS0.setSys_Mode(integer3);
        assertEquals(wBS0, WBS.launch(integer0,boolean0,boolean1,integer1,boolean1,boolean0,integer2,boolean0,boolean0));
    }

    @Test
    public void test_launch_5() {
        Integer integer0 = 2;
        Boolean boolean0 = true;
        Integer integer1 = 3;
        Boolean boolean1 = false;
        Integer integer2 = 1;
        WBS wBS0 = new WBS();
        Integer integer3 = 0;
        wBS0.setWBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE(integer3);
        wBS0.setWBS_Node_WBS_BSCU_rlt_PRE1(integer3);
        wBS0.setWBS_Node_WBS_rlt_PRE2(integer3);
        wBS0.setNor_Pressure(integer3);
        wBS0.setAlt_Pressure(integer3);
        wBS0.setSys_Mode(integer3);
        assertEquals(wBS0, WBS.launch(integer0,boolean0,boolean0,integer1,boolean1,boolean0,integer2,boolean1,boolean0));
    }

    @Test
    public void test_launch_6() {
        Integer integer0 = 1;
        Boolean boolean0 = false;
        Integer integer1 = 0;
        Boolean boolean1 = true;
        Integer integer2 = 4;
        WBS wBS0 = new WBS();
        wBS0.setWBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE(integer1);
        wBS0.setWBS_Node_WBS_BSCU_rlt_PRE1(integer2);
        Integer integer3 = 5;
        wBS0.setWBS_Node_WBS_rlt_PRE2(integer3);
        wBS0.setNor_Pressure(integer3);
        wBS0.setAlt_Pressure(integer1);
        wBS0.setSys_Mode(integer1);
        assertEquals(wBS0, WBS.launch(integer0,boolean0,boolean0,integer1,boolean1,boolean1,integer2,boolean0,boolean0));
    }

    @Test
    public void test_launch_7() {
        Integer integer0 = 1;
        Boolean boolean0 = true;
        Boolean boolean1 = false;
        Integer integer1 = 2;
        Integer integer2 = 3;
        WBS wBS0 = new WBS();
        Integer integer3 = 0;
        wBS0.setWBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE(integer3);
        wBS0.setWBS_Node_WBS_BSCU_rlt_PRE1(integer3);
        wBS0.setWBS_Node_WBS_rlt_PRE2(integer3);
        wBS0.setNor_Pressure(integer3);
        wBS0.setAlt_Pressure(integer3);
        wBS0.setSys_Mode(integer3);
        assertEquals(wBS0, WBS.launch(integer0,boolean0,boolean1,integer1,boolean1,boolean1,integer2,boolean0,boolean0));
    }

    @Test
    public void test_launch_8() {
        Integer integer0 = 0;
        Boolean boolean0 = false;
        Integer integer1 = 5;
        Boolean boolean1 = true;
        WBS wBS0 = new WBS();
        wBS0.setWBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE(integer0);
        Integer integer2 = 1;
        wBS0.setWBS_Node_WBS_BSCU_rlt_PRE1(integer2);
        wBS0.setWBS_Node_WBS_rlt_PRE2(integer2);
        wBS0.setNor_Pressure(integer2);
        wBS0.setAlt_Pressure(integer0);
        wBS0.setSys_Mode(integer0);
        assertEquals(wBS0, WBS.launch(integer0,boolean0,boolean0,integer1,boolean0,boolean1,integer0,boolean1,boolean0));
    }

    @Test
    public void test_launch_9() {
        Integer integer0 = 0;
        Boolean boolean0 = true;
        Integer integer1 = 4;
        Boolean boolean1 = false;
        Integer integer2 = 2;
        WBS wBS0 = new WBS();
        wBS0.setWBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE(integer0);
        wBS0.setWBS_Node_WBS_BSCU_rlt_PRE1(integer0);
        wBS0.setWBS_Node_WBS_rlt_PRE2(integer0);
        wBS0.setNor_Pressure(integer0);
        wBS0.setAlt_Pressure(integer0);
        wBS0.setSys_Mode(integer0);
        assertEquals(wBS0, WBS.launch(integer0,boolean0,boolean0,integer1,boolean1,boolean1,integer2,boolean0,boolean0));
    }

}
