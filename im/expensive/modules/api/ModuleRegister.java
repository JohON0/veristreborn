/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.modules.api;

import im.expensive.modules.api.Category;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value=RetentionPolicy.RUNTIME)
public @interface ModuleRegister {
    public String name();

    public String desc() default "\u0423 \u044d\u0442\u043e\u0433\u043e \u043c\u043e\u0434\u0443\u043b\u044f \u043d\u0435\u0442 \u043e\u043f\u0438\u0441\u0430\u043d\u0438\u044f.";

    public int key() default 0;

    public Category category();
}

