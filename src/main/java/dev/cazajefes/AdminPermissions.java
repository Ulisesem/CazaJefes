package dev.cazajefes;
import java.util.function.Predicate;
final class AdminPermissions {
 static boolean allows(Predicate<String> permission,String legacy){return permission.test("cazajefes.admin")||permission.test("cazajefes.admin.*")||permission.test("rareitemhunter.admin.*")||permission.test(legacy)||permission.test(legacy.replace("rareitemhunter.admin.","cazajefes.admin."));}
}
