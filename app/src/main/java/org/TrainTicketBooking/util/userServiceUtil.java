package org.TrainTicketBooking.util;

import org.mindrot.jbcrypt.BCrypt;

public class userServiceUtil {

     public static boolean checkPassword(String plainPassword,String hashedPassword){
         return  BCrypt.checkpw(plainPassword,hashedPassword);
     }

     public static String hashPassword(String password){
         return BCrypt.hashpw(password,BCrypt.gensalt());
     }

}
