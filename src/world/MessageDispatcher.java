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
package world;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import world.model.KnowledgeAwareInterface;
import world.model.Target;
import world.uav.Attacker;

/**
 *
 * @author Yulin_Zhang
 */
public abstract class MessageDispatcher {

    protected Map<Integer, LinkedList<Message>> recv_msg_list = new HashMap<Integer, LinkedList<Message>>();
    protected int num_of_msg_sent_this_time_step = 0;
    protected int num_of_msg_sent_total = 0;
    protected KnowledgeAwareInterface intelligent_unit;

    public MessageDispatcher(KnowledgeAwareInterface intelligent_unit) {
        this.intelligent_unit = intelligent_unit;
    }
    /** register the information requirement according to its current location and target.
     * 
     * @param uav_index
     * @param current_loc
     * @param target 
     */
    public abstract void register(Integer uav_index, float[] current_loc,Target target);
    
    /** make decision for information sharing.
     * 
     */
    public abstract void decideAndSumitMsgToSend();

    /** dispatch the information to the receivers.
     * 
     */
    public void dispatch() {
        List<Attacker> attackers = World.getAttackers();
        int attacker_num = attackers.size();
        for (int i = 0; i < attacker_num; i++) {
            Attacker attacker = attackers.get(i);
            Integer attacker_index = attacker.getIndex();
            LinkedList<Message> recv_list = recv_msg_list.get(attacker_index);
            if (recv_list != null && recv_list.size() > 0) {
                int total_msg_sent = recv_list.size();
                for (int j = 0; j < total_msg_sent; j++) {
                    Message msg=recv_list.get(j);
                    int msg_size=msg.getMsgSize();
                    attacker.receiveMesage(msg);
                    this.num_of_msg_sent_total += msg_size;
                    this.num_of_msg_sent_this_time_step +=  msg_size;
                }

            }
        }
        this.clearRecvMsgList();
    }

    /** return the number of msgs sent in this time step.
     * 
     * @return 
     */
    public int getNumOfMsgLatestSent() {
        return this.num_of_msg_sent_this_time_step;
    }

    /** return the total number msgs sent until now.
     * 
     * @return 
     */
    public int getTotalNumOfMsgSent() {
        return this.num_of_msg_sent_total;
    }

    /** clear the msg receive list.
     * 
     */
    public void clearRecvMsgList() {
        this.recv_msg_list.clear();
    }

    /** add the msg to the receive queue for give attacker
     * 
     * @param recv_attacker_index
     * @param msg 
     */
    public void addRecvMessage(Integer recv_attacker_index, Message msg) {
        LinkedList<Message> recv_list = this.recv_msg_list.get(recv_attacker_index);
        if (recv_list == null) {
            recv_list = new LinkedList<Message>();
        }
        recv_list.add(msg);
        this.recv_msg_list.put(recv_attacker_index, recv_list);
    }
}
