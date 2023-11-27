/*
 * Copyright 2021-2023 hligaty
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.hligaty.haibaracp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.StringJoiner;

/**
 * Configuration properties for connect pool.
 *
 * @author hligaty
 */
@ConfigurationProperties("sftp.pool")
public class PoolProperties {
    /**
     * Target for the minimum number of idle connections to maintain in the pool. This
     * setting only has an effect if both it and time between eviction runs are
     * positive.
     */
    private int minIdle = 1;

    /**
     * Maximum number of "idle" connections in the pool. Use a negative value to
     * indicate an unlimited number of idle connections.
     */
    private int maxIdle = 8;

    /**
     * Maximum number of connections that can be allocated by the pool at a given
     * time. Use a negative value for no limit.
     */
    private int maxActive = 8;

    /**
     * Maximum amount of time a connection allocation should block before throwing an
     * exception when the pool is exhausted. Use a negative value to block
     * indefinitely.
     */
    private long maxWait = -1;

    /**
     * Whether objects borrowed from the pool will be validated. Validation is
     * performed by the validateConnect() method of the SftpClient. If the object
     * fails to validate, it will be removed from the pool and destroyed, and a
     * new attempt will be made to borrow an object from the pool.
     */
    private boolean testOnBorrow = true;

    /**
     * Whether objects borrowed from the pool will be validated when they are returned
     * to the pool. Validation is performed by the validateConnect() method of the
     * SftpClient. Returning objects that fail validation are destroyed rather then
     * being returned the pool.
     */
    private boolean testOnReturn = false;

    /**
     * Whether objects sitting idle in the pool will be validated by the idle object
     * evictor. Validation is performed by the validateConnect() method of the
     * SftpClient. If the object fails to validate, it will be removed from the pool
     * and destroyed.
     */
    private boolean testWhileIdle = true;

    /**
     * Time between runs of the idle object evictor thread. When positive, the idle
     * object evictor thread starts, otherwise no idle object eviction is performed.
     */
    private long timeBetweenEvictionRuns = 1000L * 60L * 10L;

    /**
     * Returns the minimum amount of time an object may sit idle in the pool before
     * it is eligible for eviction by the idle object evictor. When non-positive,
     * no objects will be evicted from the pool due to idle time alone.
     */
    private long minEvictableIdleTimeMillis = 1000L * 60L * 30L;

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public int getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    public long getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(long maxWait) {
        this.maxWait = maxWait;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public long getTimeBetweenEvictionRuns() {
        return timeBetweenEvictionRuns;
    }

    public void setTimeBetweenEvictionRuns(long timeBetweenEvictionRuns) {
        this.timeBetweenEvictionRuns = timeBetweenEvictionRuns;
    }

    public long getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PoolProperties.class.getSimpleName() + "[", "]")
                .add("minIdle=" + minIdle)
                .add("maxIdle=" + maxIdle)
                .add("maxActive=" + maxActive)
                .add("maxWait=" + maxWait)
                .add("testOnBorrow=" + testOnBorrow)
                .add("testOnReturn=" + testOnReturn)
                .add("testWhileIdle=" + testWhileIdle)
                .add("timeBetweenEvictionRuns=" + timeBetweenEvictionRuns)
                .add("minEvictableIdleTimeMillis=" + minEvictableIdleTimeMillis)
                .toString();
    }
}
