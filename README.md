# PSLG Triangulation Demonstrator

This program shows how a trapezoidalization sweep algorithm applied to a Planar Straight Line Graph (PSLG) can be used to split it into monotone mountains which can then be triangulated in O(n) time, allowing for an O(n log n) time complex algorithm for triangulating PSLGs generally. The program has 4 stages: DRAWING, SWEEPING, TRIANGULATING, and FINAL.

In the DRAWING stage, users input a PSLG. Points can be added by left clicking and right-clicking removes the closest point to the cursor. Edges can also be toggle by middle-clicking. The edge to be added or removed is highlighted in blue. The program determines this edge by going to the nearest point and picking which edge makes the closest angle with the point as compared to the cursor.

In the SWEEPING stage, the program allows the user to go step by step through the processing of the Event Queue for the trapezoidalization sweep algorithm. Each point is processed in y-coordinate order to find the closest edge to its left and right. Using these trapezoidalization lines, the algorithm figures out which points to add new edges between in order to split the PSLG into monotone mountains.

In the TRIANGULATING stage, the program shows the monotone mountains constructed in the SWEEPING stage that are going to be triangulated in the final triangulation

In the FINAL stage, the triangulation of the PSLG is shown.
