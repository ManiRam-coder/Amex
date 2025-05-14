# CronScheduler
CronScheduler is a Java utility that allows you to schedule and execute shell commands based on cron-like expressions. It reads commands from a file, parses the cron expressions, and schedules them for execution at the specified times.

# Example commands.txt
02 07 14 5 2025 date && echo "Executes on 14th May 2025 at 07:02"

*/6 * 14 5 2025 date && echo "Executes every 6 minutes on 14th May 2025"

# Output:
14-05-2025 07:02:06:21 "Executes on 14th May 2025 at 07:02"

14-05-2025 07:02:06:22 "Executes every 6 minutes on 14th May 2025"

14-05-2025 07:08:06:23 "Executes every 6 minutes on 14th May 2025"

14-05-2025 07:14:06:24 "Executes every 6 minutes on 14th May 2025"
