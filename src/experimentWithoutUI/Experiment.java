/* 
 * Copyright (c) Yulin Zhang
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package experimentWithoutUI;

import config.NonStaticInitConfig;
import world.World;

/**
 *
 * @author Yulin_Zhang
 */
public class Experiment {

    public static void main(String[] args) {
        NonStaticInitConfig config = new NonStaticInitConfig();
        int threat_num = 5;
        int scout_num = 5;
        int attacker_num = 5;
        int[] attacker_num_list = {5, 10, 20, 50};
        int attacker_test_size = attacker_num_list.length;
        int[] obstacle_num_list = {10, 15, 21};
        int obstacle_num = 21;
        int obstacle_test_size = obstacle_num_list.length;

//        //test different scale
//        config.setScout_num(scout_num);
//        NonStaticInitConfig.obstacle_num = obstacle_num;
//        config.initObstacles();
//        for (int i = 0; i < attacker_test_size; i++) {
//            attacker_num = attacker_num_list[i];
//            threat_num = attacker_num;
//            config.setAttacker_num(attacker_num);
//            config.setThreat_num(threat_num);
//            config.initThreats();
//            for (int algorithm = 0; algorithm < 3; algorithm++) {
//                config.setInforshare_algorithm(algorithm);
//                World world = new World(config);
//                boolean experiment_over = world.isExperiment_over();
//                while (!experiment_over) {
//                    world.updateAll();
//                    experiment_over = world.isExperiment_over();
//                }
//            }
//        }

        //test different obstacle density
        config.setScout_num(scout_num);
        attacker_num = 5;
        threat_num = 5;
        config.setAttacker_num(attacker_num);
        config.setThreat_num(threat_num);
        config.initThreats();
        for (int i = 1; i < obstacle_test_size; i++) {
            obstacle_num = obstacle_num_list[i];
            NonStaticInitConfig.obstacle_num = obstacle_num;
            config.initObstacles();
            for (int algorithm = 0; algorithm < 3; algorithm++) {
                config.setInforshare_algorithm(algorithm);
                World world = new World(config);
                while (!world.isExperiment_over()) {
                    world.updateAll();
                }
            }
        }

//        //test different scout
//        int[] scout_num_list = {3, 5, 10};
//        threat_num = 5;
//        attacker_num = 5;
//        config.setAttacker_num(attacker_num);
//        config.setThreat_num(threat_num);
//        config.initObstacles();
//        config.initThreats();
//        int scout_test_size = scout_num_list.length;
//        for (int i = 0; i < scout_test_size; i++) {
//            scout_num = scout_num_list[i];
//            scout_num = scout_num_list[i];
//            config.setScout_num(scout_num);
//            for (int algorithm = 0; algorithm < 3; algorithm++) {
//                config.setInforshare_algorithm(algorithm);
//                World world = new World(config);
//                while (!world.isExperiment_over()) {
//                    world.updateAll();
//                }
//            }
//        }
    }
}
