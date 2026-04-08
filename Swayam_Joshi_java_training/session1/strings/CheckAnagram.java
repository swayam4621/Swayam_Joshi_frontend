public class CheckAnagram{
    public static boolean areAnagrams(String s1, String s2) {
       
        s1 = s1.replaceAll("\\s", "").toLowerCase();
        s2 = s2.replaceAll("\\s", "").toLowerCase();
        
        
        if (s1.length() != s2.length()) return false;
        
        char[] array1 = s1.toCharArray();
        char[] array2 = s2.toCharArray();
        
        Arrays.sort(array1);
        Arrays.sort(array2);
        
        return Arrays.equals(array1, array2);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter first string: ");
        String word1 = sc.nextLine();
        System.out.print("Enter second string: ");
        String word2 = sc.nextLine();
        
        if (areAnagrams(word1, word2)) {
            System.out.println(word1 + " and " + word2 + " are anagrams");
        } else {
            System.out.println("they are not anagrams");
        }
        sc.close();
    }
}