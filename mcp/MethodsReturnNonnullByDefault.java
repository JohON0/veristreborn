/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package mcp;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierDefault;

@Documented
@Nonnull
@TypeQualifierDefault(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface MethodsReturnNonnullByDefault {
}

