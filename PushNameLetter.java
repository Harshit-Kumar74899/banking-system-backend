import java.util.Stack;
class PushNameLetter{
    public static void main(String args[]){
        String name = "Harshit";
         Stack<Character> stack = new Stack<>();
        for (int i = 0; i < name.length(); i++) {
            stack.push(name.charAt(i));
        } 
        System.out.println("Stack after pushing all letters of the name \"" + name + "\":");
        System.out.println(stack);

        System.out.print("Reversed name by popping from stack: ");
        while (!stack.isEmpty()) {
            System.out.print(stack.pop());
        }
         for (int i = 0; i < name.length(); i++) {
            stack.pop(name.charAt(i));
        } 
        System.out.println("Stack after pushing all letters of the name \"" + name + "\":");
        System.out.println(stack);

        System.out.print("Reversed name by popping from stack: ");
        while (!stack.isFull()) {
            System.out.print(stack.push());
        }
        System.out.println();
    }
}
   