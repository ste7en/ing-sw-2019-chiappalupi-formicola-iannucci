package it.polimi.ingsw;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.List;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

    @Test
    public void toList() {
        String str1 = "abc";
        String str2 = "abc, def, ghe";
        String str3 = "";
        List<String> list1 = List.of(str1.split(", "));
        List<String> list2 = List.of(str2.split(", "));
        List<String> list3 = List.of(str3.split(", "));
        System.out.println(str1 + ": ");
        for(String s : list1) {
            System.out.println("list1[" + list1.indexOf(s) + "]" + ": " + s);
        }
        System.out.println(str2 + ": ");
        for(String s : list2) {
            System.out.println("list2[" + list2.indexOf(s) + "]" + ": " + s);
        }
        System.out.println(str3 + ": ");
        for(String s : list3) {
            System.out.println("list3[" + list3.indexOf(s) + "]" + ": " + s);
        }
    }

}
