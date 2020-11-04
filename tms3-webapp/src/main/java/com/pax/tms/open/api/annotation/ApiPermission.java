package com.pax.tms.open.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Requires the current executor's Subject to imply a particular permission in
 * order to execute the annotated method. If the executor's associated
 * {@link org.apache.shiro.subject.Subject Subject} determines that the executor
 * does not imply the specified permission, the method will not be executed.
 * </p>
 *
 * <p>
 * For example, this declaration:
 * <p/>
 * <code>&#64;RequiresPermissions( {"file:read", "write:aFile.txt"} )<br/>
* void someMethod();</code>
 * <p/>
 * indicates the current user must be able to both <tt>read</tt> and
 * <tt>write</tt> to the file <tt>aFile.txt</tt> in order for the
 * <tt>someMethod()</tt> to execute, otherwise an
 * {@link org.apache.shiro.authz.AuthorizationException AuthorizationException}
 * will be thrown.
 *
 * @see org.apache.shiro.subject.Subject#checkPermission
 * @since 0.1
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiPermission {

	/**
	 * The permission string which will be passed to
	 * {@link org.apache.shiro.subject.Subject#isPermitted(String)} to determine
	 * if the user is allowed to invoke the code protected by this annotation.
	 */
	String value();

}
